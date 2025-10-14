package com.github.onran0.passer.crypto;

import java.util.List;

public interface IKDF {
    String PBKDF2 = "PBKDF2";

    List<String> ALGORITHMS = List.of(PBKDF2);

    String getID();

    void setSalt(byte[] salt);

    byte[] getSalt();

    void setIterations(int iterations);

    int getIterations();

    void setOutputLength(int outputLength);

    int getOutputLength();

    int getSaltLength();

    byte[] getDerivedKey(char[] material);
}