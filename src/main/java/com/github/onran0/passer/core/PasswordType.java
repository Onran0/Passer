package com.github.onran0.passer.core;

public enum PasswordType {
    TEXT(0, "text"),
    BINARY(1, "binary");

    private final int id;
    private final String name;

    PasswordType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static PasswordType fromID(int id) {
        for (PasswordType p : PasswordType.values()) {
            if (p.getID() == id)
                return p;
        }

        return null;
    }
}