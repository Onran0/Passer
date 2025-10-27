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
                sb.append("Argument      Description\n--------      -----------\n");

                for(var arg : definedArgs) {
                    sb.append("[");
                    sb.append(arg.name);
                    sb.append("] ");
                    sb.append(" ".repeat(DESIRED_COLUMN_SEPARATOR_WIDTH));
                    sb.append(arg.desc);
                    sb.append('\n');
                }

                sb.append('\n');
            }

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