package com.github.onran0.passer.crypto;

import java.security.GeneralSecurityException;

public interface ISymmetricCipher extends ICipher{

    void setKey(byte[] key);

    byte[] getKey();

    int getIVSizeInBytes();

    void setIV(byte[] iv);

    byte[] getIV();

    byte[] encrypt(byte[] plaintext) throws GeneralSecurityException;

    byte[] decrypt(byte[] ciphertext) throws GeneralSecurityException;
}