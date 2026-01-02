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
import com.github.onran0.passer.security.RuntimeSecurity;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import static com.github.onran0.passer.cli.Colors.*;
import static com.github.onran0.passer.cli.Colors.RESET;

public class ListCommand extends Command {

    @Override
    protected boolean openedFileRequired() { return true; }

    @Override
    protected void initializeParser(OptionParser parser, NonOptionArgumentsParser arguments) {}

    @Override
    protected void execute(OptionSet options) {
        // TODO: do something with magic numbers

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

        for(int i = 0;i < getCore().getPasses().getPasses().size();i++) {
            final PasswordInfo passwordInfo = getCore().getPasses().getPasses().get(i);

            char[] caption = passwordInfo.getCaption();
            char[] service = passwordInfo.getService();
            char[] login = passwordInfo.getLogin();

            var builder = new StringBuilder();

            builder.append("%d%s".formatted(
                    i,
                    " ".repeat(ID_LENGTH - ((int) Math.log10(Math.max(1, i))) + 1)
            ));

            builder.append(caption);
            builder.append(" ".repeat(
                    CAPTION_LENGTH - caption.length + 7
            ));

            builder.append(login);
            builder.append(" ".repeat(
                    LOGIN_LENGTH - login.length + 5
            ));

            builder.append(service);
            builder.append('\n');

            out().print(builder);

            // trick for fully value clear

            builder.setLength(builder.length() + 1);
            builder.setLength(0);

            //

            RuntimeSecurity.clear(caption);
            RuntimeSecurity.clear(login);
            RuntimeSecurity.clear(service);
        }
    }
}