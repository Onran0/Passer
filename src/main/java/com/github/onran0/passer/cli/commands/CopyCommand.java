package com.github.onran0.passer.cli.commands;

import com.github.onran0.passer.cli.commands.core.Command;

import com.github.onran0.passer.cli.commands.core.NonOptionArgumentsParser;
import com.github.onran0.passer.core.PasswordInfo;
import com.github.onran0.passer.core.PasswordType;

import com.github.onran0.passer.security.RuntimeSecurity;

import com.github.onran0.passer.util.Convert;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import java.nio.charset.StandardCharsets;

import static com.github.onran0.passer.cli.Colors.RED;
import static com.github.onran0.passer.cli.Colors.RESET;

public class CopyCommand extends Command {

    @Override
    protected boolean openedFileRequired() { return true; }

    @Override
    protected void initializeParser(OptionParser parser, NonOptionArgumentsParser arguments) {
        arguments.defineArgument("id", Integer.class, "Password ID", true);
    }

    @Override
    protected void execute(OptionSet options) {
        int id = (Integer) getArgumentsParser().get(0);

        PasswordInfo passwordInfo = getCore().getPasses().getPasses().get(id);

        int[] passTypeRaw = passwordInfo.getPasswordType();

        String password;

        final PasswordType passType = PasswordType.fromID(passTypeRaw[0]);

        if(passType == null) {
            out().println(RED + "undefined password type" + RESET);
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
                out().println(RED + "unknown password type" + RESET);
                return;
        }

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(password), null);

        RuntimeSecurity.clear(binaryPassword);
        RuntimeSecurity.clear(passTypeRaw);

        out().println("password has been copied to the clipboard and will be erased from there in 25 seconds");

        getCore().eraseClipboardAfter(25000);
    }
}