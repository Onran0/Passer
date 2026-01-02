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

import joptsimple.BuiltinHelpFormatter;
import joptsimple.HelpFormatter;
import joptsimple.OptionSet;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class NonOptionArgumentsParser {

    private final List<NonOptionArgInfo> definedArgs = new ArrayList<>();
    private int requiredArgsCount;
    private final List<Object> arguments = new ArrayList<>();

    public int argumentsCount() {
        return arguments.size();
    }

    public Object get(int position) {
        return arguments.get(position);
    }

    public void parse(OptionSet options) {
        arguments.clear();

        var args = (List<String>) options.nonOptionArguments();

        if(args.size() < requiredArgsCount) {
            var missingArgs = new String[requiredArgsCount];

            for(int i = 0;i < requiredArgsCount;i++)
                missingArgs[i] = definedArgs.get(i).name;

            throw new NotEnoughMandatoryArguments(missingArgs);
        }

        for(int i = 0;i < Math.min(args.size(), this.definedArgs.size());i++) {
            var arg = args.get(i);
            var argInfo = this.definedArgs.get(i);

            if(argInfo.type != String.class) {
                Method method;

                try {
                    method = argInfo.type.getMethod("valueOf", String.class);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }

                try {
                    arguments.add(method.invoke(null, arg));
                } catch (Exception e) {
                    throw new InvalidArgumentValue(arg, e);
                }
            } else arguments.add(arg);
        }
    }

    public void defineArgument(String name, Class<?> type, String desc, boolean required) {
        if(required && !definedArgs.isEmpty() && !definedArgs.get(definedArgs.size() - 1).required)
            throw new IllegalArgumentException("The previous argument must be mandatory in order to make this argument mandatory");

        if(required)
            requiredArgsCount++;

        definedArgs.add(new NonOptionArgInfo(name, desc, type, required));
    }

    public HelpFormatter formatter() {
        final int DESIRED_OVERALL_WIDTH = 80;
        final int DESIRED_COLUMN_SEPARATOR_WIDTH = 2;

        BuiltinHelpFormatter builtin = new BuiltinHelpFormatter(DESIRED_OVERALL_WIDTH, DESIRED_COLUMN_SEPARATOR_WIDTH);

        return options -> {
            var sb = new StringBuilder();

            if(!definedArgs.isEmpty()) {
                int maxLengthOfName = 0;
                boolean hasRequired = false;

                var names = new String[definedArgs.size()];

                for(int i = 0;i < names.length;i++) {
                    var arg = definedArgs.get(i);

                    var name = arg.required ? "*" : "";

                    if(arg.required)
                        hasRequired = true;

                    name += arg.name;
                    name += " <";
                    name += arg.type.getSimpleName();
                    name += ">";

                    if(name.length() > maxLengthOfName)
                        maxLengthOfName = name.length();

                    names[i] = name;
                }

                var firstColumnName = "Argument";

                if(hasRequired)
                    firstColumnName += " (* = required)";

                sb.append(firstColumnName);

                var descIndent = " ".repeat(Math.max(maxLengthOfName, firstColumnName.length()) - firstColumnName.length() + DESIRED_COLUMN_SEPARATOR_WIDTH);

                sb.append(descIndent);

                var secondColumnName = "Description";

                sb.append(secondColumnName);

                sb.append('\n');

                sb.append("-".repeat(firstColumnName.length()));
                sb.append(descIndent);
                sb.append("-".repeat(secondColumnName.length()));

                sb.append('\n');

                for(int i = 0;i < definedArgs.size();i++) {
                    sb.append(names[i]);
                    sb.append(" ".repeat(Math.max(maxLengthOfName, firstColumnName.length()) - names[i].length() + DESIRED_COLUMN_SEPARATOR_WIDTH));
                    sb.append(definedArgs.get(i).desc);
                    sb.append('\n');
                }
            } else sb.append("No arguments specified");

            sb.append('\n');

            sb.append(builtin.format(options));

            return sb.toString();
        };
    }

    private static class NonOptionArgInfo {
        String name;
        String desc;
        Class<?> type;
        boolean required;

        NonOptionArgInfo(String name, String desc, Class<?> type, boolean required) {
            this.type = type;
            this.desc = desc;
            this.name = name;
            this.required = required;
        }
    }
}