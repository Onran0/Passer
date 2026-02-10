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

public class CopyLoginCommand extends Command {

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

        char[] login = passwordInfo.getLogin();

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(new String(login)), null);

        RuntimeSecurity.clear(login);

        out().println("login has been copied to the clipboard and will be erased from there in 20 seconds");

        getCore().eraseClipboardAfter(20000);
    }
}