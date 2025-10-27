package com.github.onran0.passer.cli.commands.core;

import com.github.onran0.passer.core.PasserCore;

import static com.github.onran0.passer.cli.Colors.RED;
import static com.github.onran0.passer.cli.Colors.RESET;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.*;

public final class CommandsExecutor {

    private static final Pattern TOKEN_PATTERN =
            Pattern.compile("\"((?:\\\\.|[^\"])*)\"|'((?:\\\\.|[^'])*)'|(\\S+)");

    private final PasserCore core;
    private PrintWriter out;

    private final Map<String, Command> commands = new HashMap<>();

    private String executingCommand;

    public CommandsExecutor(PasserCore core) {
        this.core = core;
    }

    public void setOutput(PrintWriter out) {
        this.out = out;
    }

    public void addCommand(String name, Command command) {
        commands.put(name, command);

        command.setInvalidUsageHandler(this::invalidUsage);
    }

    public Command getCommand(String name) {
        return commands.get(name);
    }

    private void invalidUsage(String errorMessage) {
        UsageCommand usage = (UsageCommand) commands.get("usage");

        out.print(RED + "invalid usage: " + RESET);
        out.println(errorMessage);

        if(usage != null && executingCommand != null) {
            usage.setCommand(executingCommand);
            usage.preExecute(null);
        }
    }

    public void execute(String line) {
        final var tokens = parseTokens(line);

        Command command = commands.get(executingCommand = tokens.remove(0));

        if(command == null)
            invalidUsage("undefined command \"" + executingCommand + "\"");
        else {
            command.setCore(core);
            command.setOutput(out);
            command.preExecute(command.getParser().parse(tokens.toArray(new String[0])));
        }

        executingCommand = null;

        tokens.clear();
    }

    private List<String> parseTokens(String line) {
        List<String> tokens = new ArrayList<>();

        Matcher m = TOKEN_PATTERN.matcher(line);

        while (m.find()) {
            String token = m.group(1);
            if (token == null) token = m.group(2);
            if (token == null) token = m.group(3);
            token = token.replace("\\\"", "\"").replace("\\'", "'");
            tokens.add(token);
        }

        return tokens;
    }
}