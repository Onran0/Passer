package com.github.onran0.passer.cli.commands.core;

public final class InvalidArgumentValue extends RuntimeException {

    public InvalidArgumentValue(String value, Exception cause) {
        super(value, cause);
    }
}