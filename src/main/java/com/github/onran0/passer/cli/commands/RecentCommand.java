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