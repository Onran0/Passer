package com.github.onran0.passer.crypto;

import java.util.List;

public interface ICipher {
    String AES_CBC = "AES/CBC";
    String AES_GCM = "AES/GCM";

    List<String> ALGORITHMS = List.of(AES_CBC, AES_GCM);

    String getID();
}