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

import java.util.Arrays;

import static com.github.onran0.passer.cli.Colors.RED;
import static com.github.onran0.passer.cli.Colors.RESET;

public class CloseCommand extends Command {

    @Override
    protected boolean openedFileRequired() { return true; }

    @Override
    protected void initializeParser(OptionParser parser, NonOptionArgumentsParser arguments) {
        parser.acceptsAll(Arrays.asList("f", "forced"), "Force close file (without mandatory saving)");
    }

    @Override
    protected void execute(OptionSet options) {
        if(!getCore().isSaved() && !options.has("forced")) {
            out().println(RED + "to close a file without saving, you need to specify -f/--forced option" + RESET);
            return;
        }

        getCore().closeFile();

        out().println("file closed");
    }
}