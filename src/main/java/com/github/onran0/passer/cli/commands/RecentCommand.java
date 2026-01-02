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
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.File;

public class RecentCommand extends Command {

    @Override
    protected void initializeParser(OptionParser parser, NonOptionArgumentsParser arguments) {}

    @Override
    protected void execute(OptionSet options) {
        for(File recentFile : getCore().getRecentPassesFiles())
            out().printf("%s (%s)\n", recentFile.getName(), recentFile.getAbsolutePath());
    }
}