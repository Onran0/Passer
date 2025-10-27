package com.github.onran0.passer.cli.commands;

import com.github.onran0.passer.cli.commands.core.Command;
import com.github.onran0.passer.cli.commands.core.NonOptionArgumentsParser;
import com.github.onran0.passer.core.PasserCore;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class VersionCommand extends Command {

    @Override
    protected void initializeParser(OptionParser parser, NonOptionArgumentsParser arguments) {}

    @Override
    protected void execute(OptionSet options) {
        out().println(PasserCore.VERSION);
    }
}