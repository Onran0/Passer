/*
 *     Passer - is a minimalist CLI password manager focused on security, transparency, and full control over your data
 *     Copyright (C) 2026  Onran
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.onran0.passer.cli.commands;


import com.github.onran0.passer.cli.commands.core.NonOptionArgumentsParser;
import com.github.onran0.passer.cli.commands.core.PasswordBasedCommand;
import com.github.onran0.passer.security.SecuredCharArray;
import com.github.onran0.passer.util.Convert;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import javax.crypto.AEADBadTagException;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.github.onran0.passer.cli.Colors.RED;
import static com.github.onran0.passer.cli.Colors.RESET;

public class OpenCommand extends PasswordBasedCommand {

    @Override
    protected boolean requiredNotOpenedFile() { return true; }

    @Override
    protected void initializeParser(OptionParser parser, NonOptionArgumentsParser arguments) {
        arguments.defineArgument("filepath", String.class, "Path to file", false);

        parser.acceptsAll(Arrays.asList("l", "last"), "Open the last opened file");
        parser.acceptsAll(Arrays.asList("h", "hex"), "Specify password type is HEX");
    }

    @Override
    protected void execute(OptionSet options) {
        if(options.nonOptionArguments().isEmpty() && !options.has("last")) {
            invalidUsage("pass filepath");
            return;
        }

        File passesFile;

        if(options.has("last")) {
            var recentFiles = getCore().getRecentPassesFiles();

            if(recentFiles.isEmpty()) {
                invalidUsage("you have not opened any file before or the last opened file was deleted");
                return;
            }

            passesFile = recentFiles.get(recentFiles.size() - 1);
        } else passesFile = new File((String) getArgumentsParser().get(0));

        if(!passesFile.exists())
            invalidUsage("file does not exist");
        else {
            final char[] password = requestPassword(options.has("hex"));

            var masterPassword = new SecuredCharArray(
                    options.has("hex")
                    ? new String(Convert.getBinaryFromHex(password), StandardCharsets.ISO_8859_1).toCharArray()
                    : password
            );

            try {
                this.getCore().openFile(passesFile, masterPassword);
                out().println("file successfully opened");
            } catch (AEADBadTagException e) {
                out().println(RED + "invalid master password" + RESET);
            } catch(Exception e) {
                out().println(RED + "failed to load passes file: ");
                e.printStackTrace(out());
                out().print(RESET);
            }
        }
    }
}