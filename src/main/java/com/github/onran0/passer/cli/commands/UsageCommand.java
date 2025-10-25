package com.github.onran0.passer.cli.commands;

import com.github.onran0.passer.cli.commands.core.Command;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import static com.github.onran0.passer.cli.Colors.RED;
import static com.github.onran0.passer.cli.Colors.RESET;

public class UsageCommand extends Command {

    private String command;

    @Override
    protected void initializeParser(OptionParser parser) {}

    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public void execute(OptionSet options) {
        super.execute(options);

        if((options == null || options.nonOptionArguments().isEmpty()) && command == null) {
            getOut().println(RED + "pass the command name as an argument" + RESET);
            return;
        }

        final String commandName = options != null && !options.nonOptionArguments().isEmpty()
                ? (String) options.nonOptionArguments().get(0)
                : command;

        String usage = null;

        switch (commandName) {
            case "list":
            case "help":
            case "usage":
            case "exit":
            case "recent":
            case "save":
                usage = "";
                break;

            case "close":
                usage = "[optional] <forced:bool>";
                break;

            case "info":
            case "rem":
            case "copy":
                usage = "<password id:int>";
                break;

            case "make":
            case "hmake":
                usage = "<path:str>";
                break;

            case "open":
            case "hopen":
                usage = "<path:str>\n";
                usage += "pass the path to the file in the first argument, or an asterisk if you want to open the latest recent file.";
                break;

            case "add":
                usage = "<caption:str> <type:str:[text|bin]> [optional] <auto-password-gen:bool>\n";
                usage += "pass true as the third argument if you want to delegate password generation to passer using cryptographically strong randomness.";
                break;

            case "mod":
                usage = "<password id:int> <property name:str> <new value:var>\n";
                usage += "Available properties:\n";
                usage += "caption:str - password title.\n";
                usage += "service:str - the service for which this password is intended.\n";
                usage += "login:str - login for the service and this password.\n";
                usage += "password:[optional] bool - the password itself. pass true to the third argument for automatic generation.";
                break;
        }

        if(usage != null)
            getOut().printf("usage: %s %s\n\n", commandName, usage);
        else
            getOut().printf("unknown command \"%s\"\n", commandName);

        command = null;
    }
}