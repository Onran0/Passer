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