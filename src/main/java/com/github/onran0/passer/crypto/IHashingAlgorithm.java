package com.github.onran0.passer.crypto;

import java.util.List;

public interface IHashingAlgorithm {
    String SHA2 = "SHA-2";
    String SHA3 = "SHA-3";

    List<String> ALGORITHMS = List.of(SHA2, SHA3);

    String getID();

    void setHashSize(int hashSize);

    int getHashSize();

    byte[] digest(byte[] data);
}