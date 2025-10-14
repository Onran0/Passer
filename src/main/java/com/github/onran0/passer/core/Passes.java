package com.github.onran0.passer.core;

import java.util.ArrayList;
import java.util.List;

public final class Passes {

    private final List<PasswordInfo> passwordInfos;

    public Passes() {
        this(new ArrayList<>());
    }

    public Passes(final List<PasswordInfo> passwordInfos) {
        this.passwordInfos = passwordInfos;
    }

    public List<PasswordInfo> getPasses() {
        return passwordInfos;
    }
}