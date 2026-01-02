/*
 *     Passer - is a minimalist CLI password manager focused on security, transparency, and full control over your data
 *     Copyright (C) 2026  Onran
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.onran0.passer.crypto;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

public final class AESGCM implements ISymmetricCipher {

    private final static String AES_CIPHER_ID = "AES";
    private final static String AES_GCM_CIPHER_ID = "AES/GCM/NoPadding";

    private final Cipher cipher;

    private byte[] key;
    private byte[] iv;

    private SecretKey secretKey;
    private GCMParameterSpec gcmParam;

    public AESGCM() {
        try {
            this.cipher = Cipher.getInstance(AES_GCM_CIPHER_ID);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void checkInit() {
        if(key == null || iv == null)
            throw new IllegalStateException("Key or IV not set");
    }

    @Override
    public int getIVSizeInBytes() {
        return 12;
    }

    @Override
    public void setIV(byte[] iv) {
        if(iv.length != 12)
            throw new IllegalArgumentException("Invalid IV size");

        this.iv = iv;

        this.gcmParam = new GCMParameterSpec(128, iv);
    }

    @Override
    public byte[] getIV() {
        return iv;
    }

    @Override
    public void setKey(byte[] key) {
        int keyBitsCount = key.length * 8;

        if(keyBitsCount != 128 && keyBitsCount != 192 && keyBitsCount != 256)
            throw new IllegalArgumentException("Invalid key size");

        this.key = key;
        this.secretKey = new SecretKeySpec(key, AES_CIPHER_ID);
    }

    @Override
    public byte[] getKey() {
        return this.key;
    }

    @Override
    public byte[] encrypt(byte[] plaintext) throws GeneralSecurityException {
        return doFinal(Cipher.ENCRYPT_MODE, plaintext);
    }

    @Override
    public byte[] decrypt(byte[] ciphertext) throws GeneralSecurityException {
        return doFinal(Cipher.DECRYPT_MODE, ciphertext);
    }

    private byte[] doFinal(int mode, byte[] data) throws GeneralSecurityException {
        checkInit();

        cipher.init(mode, secretKey, gcmParam);
        return cipher.doFinal(data);
    }

    @Override
    public String getID() {
        return ICipher.AES_GCM;
    }
}