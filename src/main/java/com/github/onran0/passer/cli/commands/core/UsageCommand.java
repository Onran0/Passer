package com.github.onran0.passer.cli.commands.core;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.IOException;

import static com.github.onran0.passer.cli.Colors.*;

public class UsageCommand extends Command {

    private final CommandsExecutor executor;
    private String command;

    public UsageCommand(CommandsExecutor executor) {
        this.executor = executor;
    }

    @Override
    protected void initializeParser(OptionParser parser, NonOptionArgumentsParser arguments) {}

    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    protected void execute(OptionSet options) {
        if((options == null || options.nonOptionArguments().isEmpty()) && command == null) {
            out().println(RED + "pass the command name as an argument" + RESET);
            return;
        }

        final String commandName = options != null && !options.nonOptionArguments().isEmpty()
                ? (String) options.nonOptionArguments().get(0)
                : command;

        final Command command = executor.getCommand(commandName);



        if(command != null) {
            try {
                command.getParser().printHelpOn(out());
            } catch(IOException e) {
                out().print(RED + "failed to print command usage: ");
                e.printStackTrace(out());
                out().print(RESET);
            }
        } else out().printf("unknown command \"%s\"\n", commandName);
    }
}