package com.github.onran0.passer.cli;

import com.github.onran0.passer.core.PasswordInfo;
import com.github.onran0.passer.core.PasserCore;
import com.github.onran0.passer.core.Passes;
import com.github.onran0.passer.core.PasswordType;
import com.github.onran0.passer.crypto.CryptoFactory;
import com.github.onran0.passer.crypto.ICipher;
import com.github.onran0.passer.crypto.IKDF;
import com.github.onran0.passer.security.RuntimeSecurity;
import com.github.onran0.passer.security.SecuredCharArray;
import com.github.onran0.passer.util.Convert;

import javax.crypto.AEADBadTagException;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.onran0.passer.cli.Colors.*;

public class Main {

    private static final String PRINTABLE_ASCII = "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";

    private static final Pattern TOKEN_PATTERN =
            Pattern.compile("\"((?:\\\\.|[^\"])*)\"|'((?:\\\\.|[^'])*)'|(\\S+)");

    private final Console console = System.console();
    private final PrintWriter out = console.writer();
    private final PasserCore core;

    private final List<String> tokens = new ArrayList<>();
    private final Map<String, Runnable> commands = new HashMap<>();

    private File openedFile;
    private SecuredCharArray masterPassword;
    private Passes passes;
    private boolean fileSaved;

    private void help() {
        out.println("commands: help, usage, make, hmake, open, hopen, list, info, copy, add, rem, mod, save, close, recent, exit");
        out.println(
                """
                help - shows a list of all commands and their descriptions
                usage - shows the format of arguments and sometimes a more detailed description of a specific command
                make - create a new password storage
                hmake - create a new password storage with a binary master password
                open - open password storage
                hopen - open the password storage with a binary master password
                list - displays a list with information about all passwords in the storage
                info - shows information about a specific password
                copy - copies a specific password to the clipboard and erases it after a minute
                add - adds a new password to the storage
                rem - removes a password from the storage
                mod - modifies password information
                save - saves password storage
                close - closes the current password storage
                recent - shows a list of the last 10 opened password storages
                exit - terminates the program
                """
        );
    }

    private void usage() {
        if(tokensRemain() < 0) {
            out.println(RED + "pass the command name as an argument" + RESET);
            return;
        }

        usage(nextToken());
    }

    private void usage(String commandName) {
        String usage = null;

        switch (commandName) {
            case "list":
            case "help":
            case "usage":
            case "exit":
            case "recent":
            case "save":
                usage = "";
                break;

            case "close":
                usage = "[optional] <forced:bool>";
                break;

            case "info":
            case "rem":
            case "copy":
                usage = "<password id:int>";
                break;

            case "make":
            case "hmake":
            case "open":
            case "hopen":
                usage = "<path:str>";
                break;

            case "add":
                usage = "<caption:str> <type:str:[text|bin]> [optional] <auto-password-gen:bool>\n";
                usage += "pass true as the third argument if you want to delegate password generation to passer using cryptographically strong randomness.";
                break;

            case "mod":
                usage = "<password id:int> <property name:str> <new value:var>\n";
                usage += "Available properties:\n";
                usage += "caption:str - password title.\n";
                usage += "service:str - the service for which this password is intended.\n";
                usage += "login:str - login for the service and this password.\n";
                usage += "password:[optional] bool - the password itself. pass true to the third argument for automatic generation.";
                break;
        }

        if(usage != null)
            out.printf("usage: %s %s\n\n", commandName, usage);
        else
            out.printf("unknown command \"%s\"\n", commandName);
    }

    private boolean checkNotOpen() {
        boolean isOpen = openedFile != null;

        if(!isOpen)
            out.println(RED + "to use this command you need to open the file" + RESET);

        return !isOpen;
    }

    private void make(final boolean passInHex) {
        if(this.openedFile != null) {
            out.println(RED + "to make a file, you need to close the current one");
            return;
        }

        if(tokensRemain() < 1) {
            out.println(RED + "incorrect command usage" + RESET);
            usage((passInHex ? "h" : "") + "make");
            return;
        }

        final String path = nextToken();

        File passesFile = new File(path);

        if(passesFile.exists())
            out.println(RED + "file already exists" + RESET);
        else {
            final char[] password = requestPassword(passInHex);

            this.masterPassword = new SecuredCharArray(
                    passInHex
                            ? new String(Convert.getBinaryFromHex(password), StandardCharsets.ISO_8859_1).toCharArray()
                            : password
            );

            try {
                core.setPassesForFile(
                        passesFile,
                        new Passes(),
                        ICipher.AES_GCM,
                        IKDF.PBKDF2,
                        masterPassword
                );
                out.println("file successfully created");
            } catch(Exception e) {
                out.println(RED + "failed to create passes file: "+ RESET);
                e.printStackTrace();
            }
        }
    }

    private void open(final boolean passInHex) {
        if(this.openedFile != null) {
            out.println(RED + "to open a file, you need to close the current one"+ RESET);
            return;
        }

        if(tokensRemain() < 1) {
            out.println(RED + "incorrect command usage"+ RESET);
            usage((passInHex ? "h" : "") + "open");
            return;
        }

        final String path = nextToken();

        File passesFile = new File(path);

        if(!passesFile.exists())
            out.println(RED + "file does not exist" + RESET);
        else {
            final char[] password = requestPassword(passInHex);

            this.masterPassword = new SecuredCharArray(
                    passInHex
                    ? new String(Convert.getBinaryFromHex(password), StandardCharsets.ISO_8859_1).toCharArray()
                    : password
            );

            try {
                this.passes = core.getPassesForFile(passesFile, masterPassword);
                this.openedFile = passesFile;
                this.fileSaved = true;
                this.core.addPassesFileToRecent(this.openedFile);
                out.println("file successfully opened");
            } catch (AEADBadTagException e) {
                out.println(RED + "invalid master password" + RESET);
            } catch(Exception e) {
                out.println(RED + "failed to load passes file: " + RESET);
                e.printStackTrace();
            }
        }
    }

    private void list() {
        if(checkNotOpen())
            return;

        final int ID_LENGTH = 5;
        final int CAPTION_LENGTH = 40;
        final int LOGIN_LENGTH = 25;

        System.out.printf(
                "%sID%s%s%sCaption%s%s%sLogin%s%s%sService%s\n",
                CYAN, RESET,
                " ".repeat(ID_LENGTH),
                GREEN, RESET,
                " ".repeat(CAPTION_LENGTH),
                PURPLE, RESET,
                " ".repeat(LOGIN_LENGTH),
                YELLOW, RESET
        );

        for(int i = 0;i < passes.getPasses().size();i++) {
            final PasswordInfo passwordInfo = passes.getPasses().get(i);

            char[] caption = passwordInfo.getCaption();
            char[] service = passwordInfo.getService();
            char[] login = passwordInfo.getLogin();

            // TODO: do something with magic numbers

            System.out.printf("%d%s",
                    i,
                    " ".repeat(ID_LENGTH - ((int) Math.log10(Math.max(1, i))) + 1)
            );
            System.out.print(caption);
            System.out.print(" ".repeat(
                    CAPTION_LENGTH - caption.length + 7
            ));
            System.out.print(login);
            System.out.print(" ".repeat(
                    LOGIN_LENGTH - login.length + 5
            ));
            System.out.print(service);
            System.out.println();

            //

            RuntimeSecurity.clear(caption);
            RuntimeSecurity.clear(login);
            RuntimeSecurity.clear(service);
        }
    }

    private void info() {
        if(checkNotOpen())
            return;

        int id = -1;
        boolean incorrectUsage = true;

        if(tokensRemain() > 0) {
            String rawId = nextToken();

            try {
                id = Integer.parseInt(rawId);
                incorrectUsage = false;
            } catch(NumberFormatException e) {}
        }

        if(incorrectUsage) {
            out.println(RED + "incorrect command usage" + RESET);
            usage("info");
            return;
        }

        PasswordInfo passwordInfo = passes.getPasses().get(id);

        char[] caption = passwordInfo.getCaption();
        char[] login = passwordInfo.getLogin();
        char[] service = passwordInfo.getService();

        int[] type = passwordInfo.getType();
        long[] creationTime = passwordInfo.getCreationTime();
        long[] modificationTime = passwordInfo.getModificationTime();
        PasswordType typeEnum = PasswordType.fromID(type[0]);

        out.print("caption: ");
        out.print(caption);

        out.print("\nlogin: ");
        out.print(login);

        out.print("\nservice: ");
        out.print(service);

        out.print("\npassword type: " + (typeEnum == null ? "undefined" : typeEnum.getName()));

        out.printf("\ncreation time: %d", creationTime[0]);
        out.printf("\nmodification time: %d", modificationTime[0]);
        out.println();

        typeEnum = null;
        RuntimeSecurity.clear(caption);
        RuntimeSecurity.clear(service);
        RuntimeSecurity.clear(login);
        RuntimeSecurity.clear(type);
        RuntimeSecurity.clear(creationTime);
        RuntimeSecurity.clear(modificationTime);
    }

    private void copy() {
        if(checkNotOpen())
            return;

        int id = -1;
        boolean incorrectUsage = true;

        if(tokensRemain() > 0) {
            String rawId = nextToken();

            try {
                id = Integer.parseInt(rawId);
                incorrectUsage = false;
            } catch(NumberFormatException e) {}
        }

        if(incorrectUsage) {
            out.println(RED + "incorrect command usage" + RESET);
            usage("copy");
            return;
        }

        PasswordInfo passwordInfo = passes.getPasses().get(id);

        int[] passTypeRaw = passwordInfo.getType();

        String password;

        final PasswordType passType = PasswordType.fromID(passTypeRaw[0]);

        if(passType == null) {
            out.println(RED + "undefined password type" + RESET);
            return;
        }

        byte[] binaryPassword = passwordInfo.getPassword();

        switch(passType) {
            case TEXT:
                password = new String(binaryPassword, StandardCharsets.UTF_8);
                break;

            case BINARY:
                password = Convert.binaryToHex(binaryPassword);
                break;

            default:
                out.println(RED + "unknown password type" + RESET);
                return;
        }

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(password), null);

        RuntimeSecurity.clear(binaryPassword);
        RuntimeSecurity.clear(passTypeRaw);

        out.println("password has been copied to the clipboard and will be erased from there in 1 minute");

        core.eraseClipboardAfter(60000);
    }

    private void add() {
        if(checkNotOpen())
            return;

        String caption = null;
        PasswordType type = null;
        boolean incorrectUsage = true;

        byte[] password = null;

        if(tokensRemain() > 1) {
            caption = nextToken();
            String strType = nextToken();

            type = switch (strType) {
                case "text" -> PasswordType.TEXT;
                case "bin" -> PasswordType.BINARY;
                default -> null;
            };

            if(type != null) {
                if(tokensRemain() > 0 && Boolean.parseBoolean(nextToken())) {
                    char[] passwordInput = requestPassword(type == PasswordType.BINARY);

                    if(type == PasswordType.BINARY) {
                        try {
                            password = Convert.getBinaryFromHex(passwordInput);
                            incorrectUsage = false;
                        } catch(NumberFormatException ignored) {}
                    } else {
                        password = Convert.getUTF8Bytes(passwordInput);
                        incorrectUsage = false;
                    }

                    RuntimeSecurity.clear(passwordInput);
                } else incorrectUsage = false;
            }
        }

        if(incorrectUsage) {
            out.println(RED + "incorrect command usage" + RESET);
            usage("add");
            return;
        }

        if(password == null) {
            switch (type) {
                case TEXT:
                    StringBuilder randomPassword = new StringBuilder();

                    for (int i = 0; i < 16; i++) {
                        randomPassword.append(PRINTABLE_ASCII.charAt(
                                CryptoFactory.getSecureRandom().nextInt(0, PRINTABLE_ASCII.length())
                        ));
                    }

                    password = randomPassword.toString().getBytes(StandardCharsets.UTF_8);

                    break;

                case BINARY:
                    password = new byte[16];

                    CryptoFactory.getSecureRandom().nextBytes(password);
                    break;
            }
        }

        long creationTime = System.currentTimeMillis();

        this.passes.getPasses().add(new PasswordInfo(
                caption.toCharArray(),
                PasserCore.MISSING_PROPERTY_DEFAULT.clone(),
                PasserCore.MISSING_PROPERTY_DEFAULT.clone(),
                new int[] { type.ordinal() },
                password,
                new long[] { creationTime },
                new long[] { creationTime }
        ));

        out.printf("new password with caption \"%s\" successfully added and its id is \"%d\"\n", caption, this.passes.getPasses().size() - 1);

        fileSaved = false;
    }

    private void rem() {
        if(checkNotOpen())
            return;

        int id = -1;
        boolean incorrectUsage = true;

        if(tokensRemain() > 0) {
            String rawId = nextToken();

            try {
                id = Integer.parseInt(rawId);
                incorrectUsage = false;
            } catch(NumberFormatException ignored) {}
        }

        if(incorrectUsage) {
            out.println(RED + "incorrect command usage" + RESET);
            usage("rem");
            return;
        }

        PasswordInfo passwordInfo = passes.getPasses().get(id);

        passes.getPasses().remove(id);

        out.printf("password with id \"%d\" and caption \"", id);

        char[] caption = passwordInfo.getCaption();

        for(char c : caption)
            out.print(c);

        out.println("\" was removed");

        RuntimeSecurity.clear(caption);

        fileSaved = false;
    }

    private void mod() {
        if(checkNotOpen())
            return;

        int id = -1;
        String newCaption = null;
        boolean incorrectUsage = true;

        if(tokensRemain() > 1) {
            String rawId = nextToken();

            try {
                id = Integer.parseInt(rawId);
                newCaption = nextToken();
                incorrectUsage = false;
            } catch(NumberFormatException e) {}
        }

        if(incorrectUsage) {
            out.println(RED + "incorrect command usage" + RESET);
            usage("mod");
            return;
        }

        PasswordInfo passwordInfo = passes.getPasses().get(id);

        passwordInfo.setCaption(newCaption.toCharArray());
        passwordInfo.setModificationTime(new long[] { System.currentTimeMillis() });

        out.printf("caption of pass with id \"%d\" was changed to \"%s\"\n", id, newCaption);

        fileSaved = false;
    }

    private void save() {
        if(checkNotOpen())
            return;

        try {
            core.setPassesForFile(
                    this.openedFile,
                    this.passes,
                    ICipher.AES_GCM,
                    IKDF.PBKDF2,
                    masterPassword
            );

            out.println("file successfully saved");

            fileSaved = true;
        } catch(IOException | GeneralSecurityException e) {
            out.println(RED + "failed to save file: " + RESET);
            e.printStackTrace();
        }
    }

    private void close() {
        if(checkNotOpen())
            return;

        if(!fileSaved && (tokensRemain() < 1 || !Boolean.parseBoolean(nextToken()))) {
            out.println(RED + "to close a file without saving, you need to set the \"forced\" argument to true" + RESET);
            return;
        }

        this.masterPassword = null;
        this.passes = null;
        this.fileSaved = false;
        this.openedFile = null;

        out.println("file closed");
    }

    private void recent() {
        for(File recentFile : core.getRecentPassesFiles()) {
            out.printf("%s (%s)\n", recentFile.getName(), recentFile.getAbsolutePath());
        }
    }

    private Main() {
        this.core = new PasserCore();

        this.commands.put("help", this::help);
        this.commands.put("usage", this::usage);
        this.commands.put("make", () -> make(false));
        this.commands.put("hmake", () -> make(true));
        this.commands.put("open", () -> open(false));
        this.commands.put("hopen", () -> open(true));
        this.commands.put("list", this::list);
        this.commands.put("info", this::info);
        this.commands.put("copy", this::copy);
        this.commands.put("add", this::add);
        this.commands.put("rem", this::rem);
        this.commands.put("mod", this::mod);
        this.commands.put("save", this::save);
        this.commands.put("close", this::close);
        this.commands.put("recent", this::recent);
    }

    private void start() {
        core.start();

        while(true) {
            out.write(YELLOW);
            parseTokens(console.readLine(">> "));
            out.write(RESET);

            String command = nextToken();

            if("exit".equals(command)) {
                if(openedFile == null)
                    break;
                else {
                    out.printf(RED + "to exit you need to close the file%s\n", RESET);
                    continue;
                }
            }

            try {
                if(commands.containsKey(command))
                    commands.get(command).run();
                else
                    out.printf("unknown command \"%s\"\n", command);
            } catch(Exception e) {
                e.printStackTrace();
            }

            this.tokens.clear();
            System.gc();
        }

        core.stop();


    }

    private char[] requestPassword(boolean hex) {
        if(!hex)
            return console.readPassword("enter password: ");
        else
            return console.readPassword("enter password in hex: ");
    }

    private int tokensRemain() {
        return tokens.size();
    }

    private String nextToken() {
        return tokens.remove(0);
    }

    private void parseTokens(String line) {
        Matcher m = TOKEN_PATTERN.matcher(line);

        while (m.find()) {
            String token = m.group(1);
            if (token == null) token = m.group(2);
            if (token == null) token = m.group(3);
            token = token.replace("\\\"", "\"").replace("\\'", "'");
            tokens.add(token);
        }
    }

    public static void main(String[] args) {
        new Main().start();
    }
}