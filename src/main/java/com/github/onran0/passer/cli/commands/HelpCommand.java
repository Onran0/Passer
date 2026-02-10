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

public class HelpCommand extends Command {

    @Override
    protected void initializeParser(OptionParser parser, NonOptionArgumentsParser arguments) {}

    @Override
    protected void execute(OptionSet options) {
        out().println("commands: help, version, usage, make, open, list, info, copy, add, rem, mod, mred, save, close, recent, exit\n\n");
        out().println(
                """
                help - shows a list of all commands and their descriptions
                
                version - shows the current version of passer
                
                usage - shows the format of arguments and sometimes a more detailed description of a specific command
                
                make - create a new password storage
                
                hmake - create a new password storage with a binary master password
                
                open - open password storage
                
                hopen - open the password storage with a binary master password
                
                list - displays a list with information about all passwords in the storage
                
                info - shows information about a specific password
                
                copy - copies a specific password to the clipboard and erases it after a 20 seconds
                
                copyl - copies a specific login to the clipboard and erases it after a 20 seconds
                
                add - adds a new password to the storage
                
                rem - removes a password from the storage
                
                mod - modifies password information
                
                mred - change master password
                
                save - saves password storage
                
                close - closes the current password storage
                
                recent - shows a list of the last 10 opened password storages
                
                exit - terminates the program
                """
        );
    }
}