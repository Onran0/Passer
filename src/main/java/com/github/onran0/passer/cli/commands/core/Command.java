package com.github.onran0.passer.cli.commands.core;

import com.github.onran0.passer.core.PasserCore;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.PrintWriter;
import java.util.function.Consumer;

import static com.github.onran0.passer.cli.Colors.RED;
import static com.github.onran0.passer.cli.Colors.RESET;

public abstract class Command {

    private final OptionParser parser;
    private final NonOptionArgumentsParser argumentsParser;
    private PrintWriter out;
    private PasserCore core;
    private Consumer<String> invalidUsageHandler;

    public Command() {
        parser = new OptionParser();
        argumentsParser = new NonOptionArgumentsParser();
        parser.formatHelpWith(argumentsParser.formatter());
        initializeParser(parser, argumentsParser);
    }

    protected boolean openedFileRequired() { return false; }

    protected boolean requiredNotOpenedFile() { return false; }

    protected abstract void initializeParser(final OptionParser parser, final NonOptionArgumentsParser arguments);

    public void setOutput(PrintWriter out) {
        this.out = out;
    }

    public void setCore(PasserCore core) {
        this.core = core;
    }

    public void setInvalidUsageHandler(Consumer<String> invalidUsageHandler) {
        this.invalidUsageHandler = invalidUsageHandler;
    }

    protected PrintWriter out() {
        return out;
    }

    protected PasserCore getCore() {
        return core;
    }

    public OptionParser getParser() {
        return parser;
    }

    public NonOptionArgumentsParser getArgumentsParser() {
        return argumentsParser;
    }

    public void invalidUsage(String errorMessage) {
        if(invalidUsageHandler == null)
            throw new IllegalStateException("invalid usage handler is not set");

        invalidUsageHandler.accept(errorMessage);
    }

    public void preExecute(OptionSet options) {
        if(out == null)
            throw new IllegalStateException("out is not set");

        if(core == null)
            throw new IllegalStateException("core is not set");

        if(openedFileRequired() && core.getOpenedFile() == null) {
            out.println(RED + "to use this command you need to open the file" + RESET);
            return;
        }

        if(requiredNotOpenedFile() && core.getOpenedFile() != null) {
            out.println(RED + "to use this command you need to close the file" + RESET);
            return;
        }

        if(options != null)
            argumentsParser.parse(options);

        execute(options);
    }

    protected abstract void execute(OptionSet options);
}