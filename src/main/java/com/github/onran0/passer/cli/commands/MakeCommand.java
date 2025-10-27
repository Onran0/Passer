package com.github.onran0.passer.cli.commands;


import com.github.onran0.passer.cli.commands.core.NonOptionArgumentsParser;
import com.github.onran0.passer.cli.commands.core.PasswordBasedCommand;
import com.github.onran0.passer.crypto.ICipher;
import com.github.onran0.passer.crypto.IKDF;
import com.github.onran0.passer.security.SecuredCharArray;
import com.github.onran0.passer.util.Convert;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.github.onran0.passer.cli.Colors.RED;
import static com.github.onran0.passer.cli.Colors.RESET;

public class MakeCommand extends PasswordBasedCommand {

    // TODO: choose of encryption and kdf algorithm

    @Override
    protected boolean requiredNotOpenedFile() { return true; }

    @Override
    protected void initializeParser(OptionParser parser, NonOptionArgumentsParser arguments) {
        arguments.defineArgument("filepath", String.class, "path to file", true);

        parser.acceptsAll(Arrays.asList("h", "hex"), "Specify password type is HEX");
    }

    @Override
    protected void execute(OptionSet options) {
        File passesFile = new File((String) getArgumentsParser().get(0));

        if(passesFile.exists())
            out().println(RED + "file already exists" + RESET);
        else {
            final char[] password = requestPassword(options.has("hex"));

            var masterPassword = new SecuredCharArray(
                    options.has("hex")
                    ? new String(Convert.getBinaryFromHex(password), StandardCharsets.ISO_8859_1).toCharArray()
                    : password
            );

            try {
                getCore().createFile(
                        passesFile,
                        masterPassword,
                        ICipher.AES_GCM,
                        IKDF.PBKDF2
                );
                out().println("file successfully created");
            } catch(Exception e) {
                out().print(RED + "failed to create passes file: ");
                e.printStackTrace(out());
                out().print(RESET);
            }
        }
    }
}