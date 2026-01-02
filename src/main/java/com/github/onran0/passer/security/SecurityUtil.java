package com.github.onran0.passer.security;

public final class SecurityUtil {

    public static boolean passwordsAreMatch(char[] a, char[] b) {
        if(a.length != b.length)
            return false;

        for(int i = 0; i < a.length; i++) {
            if(a[i] != b[i])
                return false;
        }

        return true;
    }
}