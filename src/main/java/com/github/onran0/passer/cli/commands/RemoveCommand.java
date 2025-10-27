package com.github.onran0.passer.cli.commands;

import com.github.onran0.passer.cli.commands.core.Command;
import com.github.onran0.passer.cli.commands.core.NonOptionArgumentsParser;
import com.github.onran0.passer.core.PasswordInfo;
import com.github.onran0.passer.security.RuntimeSecurity;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class RemoveCommand extends Command {

    @Override
    protected boolean openedFileRequired() { return true; }

    @Override
    protected void initializeParser(OptionParser parser, NonOptionArgumentsParser arguments) {
        arguments.defineArgument("id", Integer.class, "Password ID", true);
    }

    @Override
    protected void execute(OptionSet options) {
        int id = (Integer) getArgumentsParser().get(0);

        var passes = getCore().getPasses();

        PasswordInfo passwordInfo = passes.getPasses().get(id);

        passes.getPasses().remove(id);

        out().printf("password with id \"%d\" and caption \"", id);

        char[] caption = passwordInfo.getCaption();

        for(char c : caption)
            out().print(c);

        out().println("\" was removed");

        RuntimeSecurity.clear(caption);

        getCore().setUnsaved();
    }
}