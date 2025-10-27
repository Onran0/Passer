package com.github.onran0.passer.cli.commands.core;

public final class NotEnoughMandatoryArguments extends RuntimeException {

    private final String[] arguments;

    public NotEnoughMandatoryArguments(String[] arguments) {
        this.arguments = arguments.clone();
    }

    public String[] getArguments() {
        return arguments.clone();
    }
}