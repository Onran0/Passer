package com.github.onran0.passer.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class SHA3 implements IHashingAlgorithm {

    private int hashSize = -1;

    @Override
    public String getID() {
        return IHashingAlgorithm.SHA3;
    }

    @Override
    public void setHashSize(int hashSize) {
        if(hashSize != 224 && hashSize != 256 && hashSize != 384 && hashSize != 512)
            throw new IllegalArgumentException("Invalid SHA-3 hash size");

        this.hashSize = hashSize;
    }

    @Override
    public int getHashSize() {
        return this.hashSize;
    }

    @Override
    public byte[] digest(byte[] data) {
        if(hashSize == -1)
            throw new IllegalStateException("Hash size not set");

        try {
            return MessageDigest.getInstance("SHA3-" + hashSize).digest(data);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}