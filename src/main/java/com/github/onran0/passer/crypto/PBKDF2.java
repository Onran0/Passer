package com.github.onran0.passer.crypto;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PBKDF2 implements IKDF {

    private static final int SALT_LENGTH = 16;

    private static final String PBKDF2_KDF_ID = "PBKDF2WithHmacSHA256";

    private byte[] salt;
    private int iterations = -1;
    private int outputLength = -1;

    private void checkInit() {
        if(
                salt == null || iterations == -1 || outputLength == -1
        ) throw new IllegalStateException("Salt, iterations or output length is not initialized");
    }

    @Override
    public String getID() {
        return IKDF.PBKDF2;
    }

    @Override
    public void setSalt(byte[] salt) {
        if(SALT_LENGTH != salt.length)
            throw new IllegalArgumentException("Invalid salt length");

        this.salt = salt;
    }

    @Override
    public byte[] getSalt() {
        return salt;
    }

    @Override
    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    @Override
    public int getIterations() {
        return iterations;
    }

    @Override
    public void setOutputLength(int outputLength) {
        this.outputLength = outputLength;
    }

    @Override
    public int getOutputLength() {
        return outputLength;
    }

    @Override
    public int getSaltLength() {
        return SALT_LENGTH;
    }

    @Override
    public byte[] getDerivedKey(char[] material) {
        Exception e = null;
        byte[] key = null;

        try {
            PBEKeySpec spec = new PBEKeySpec(material, salt, iterations, outputLength);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_KDF_ID);
            key = skf.generateSecret(spec).getEncoded();
        } catch(Exception e1) {
            e = e1;
        }

        if(e != null)
            throw new RuntimeException(e);
        else
            return key;
    }
}