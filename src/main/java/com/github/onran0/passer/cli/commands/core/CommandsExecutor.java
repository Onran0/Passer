/*
 *     Passer - is a minimalist CLI password manager focused on security, transparency, and full control over your data
 *     Copyright (C) 2026  Onran
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.onran0.passer.cli.commands.core;

import com.github.onran0.passer.core.PasserCore;
import joptsimple.OptionException;

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
        out.println();

        if(usage != null && executingCommand != null) {
            usage.setCore(core);
            usage.setOutput(out);
            usage.setCommand(executingCommand);
            usage.preExecute(null);
        }
    }

    public void execute(String line) {
        final var tokens = parseTokens(line);

        Command command = commands.get(executingCommand = tokens.remove(0));

        if(command == null)
            out.println(RED + "undefined command \"" + executingCommand + "\"" + RESET);
        else {
            command.setCore(core);
            command.setOutput(out);

            try {
                command.preExecute(command.getParser().parse(tokens.toArray(new String[0])));
            } catch(NotEnoughMandatoryArguments e) {
                var sb = new StringBuilder("skipped required arguments: ");

                var args = e.getArguments();

                for(int i = 0;i < args.length;i++) {
                    sb.append(args[i]);

                    if(i != args.length - 1)
                        sb.append(", ");
                }

                invalidUsage(sb.toString());
            } catch(OptionException e) {
                invalidUsage(e.getMessage().toLowerCase());
            } catch(InvalidArgumentValue e) {
                invalidUsage("invalid argument value: " + e.getMessage());
            }
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