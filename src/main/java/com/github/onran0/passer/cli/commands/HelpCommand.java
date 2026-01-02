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
                
                copy - copies a specific password to the clipboard and erases it after a minute
                
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