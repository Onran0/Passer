package com.github.onran0.passer.cli.commands.core;

import com.github.onran0.passer.core.PasserCore;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.PrintWriter;
import java.util.function.Consumer;

public abstract class Command {

    private final OptionParser parser;
    private PrintWriter out;
    private PasserCore core;
    private Consumer<String> invalidUsageHandler;

    public Command() {
        parser = new OptionParser();
        initializeParser(parser);
    }

    protected abstract void initializeParser(final OptionParser parser);

    public void setOutput(PrintWriter out) {
        this.out = out;
    }

    public void setCore(PasserCore core) {
        this.core = core;
    }

    public void setInvalidUsageHandler(Consumer<String> invalidUsageHandler) {
        this.invalidUsageHandler = invalidUsageHandler;
    }

    protected PrintWriter getOut() {
        return out;
    }

    protected PasserCore getCore() {
        return core;
    }

    public OptionParser getParser() {
        return parser;
    }

    public void invalidUsage(String errorMessage) {
        if(invalidUsageHandler == null)
            throw new IllegalStateException("invalid usage handler is not set");

        invalidUsageHandler.accept(errorMessage);
    }

    public void execute(OptionSet options) {
        if(out == null)
            throw new IllegalStateException("out is not set");

        if(core == null)
            throw new IllegalStateException("core is not set");
    }
}