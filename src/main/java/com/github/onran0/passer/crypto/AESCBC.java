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

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class AESCBC implements ISymmetricCipher {

    private final static String AES_CIPHER_ID = "AES";
    private final static String AES_CBC_CIPHER_ID = "AES/CBC/PKCS5Padding";

    private final Cipher cipher;

    private byte[] key;
    private byte[] iv;

    private SecretKey secretKey;
    private IvParameterSpec ivParam;

    public AESCBC() {
        try {
            this.cipher = Cipher.getInstance(AES_CBC_CIPHER_ID);
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
        return 16;
    }

    public void setIV(byte[] iv) {
        if(iv.length != 16)
            throw new IllegalArgumentException("Invalid IV size");

        this.iv = iv;

        this.ivParam = new IvParameterSpec(iv);
    }

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
    public byte[] encrypt(byte[] plaintext) {
        return doFinal(Cipher.ENCRYPT_MODE, plaintext);
    }

    @Override
    public byte[] decrypt(byte[] ciphertext) {
        return doFinal(Cipher.DECRYPT_MODE, ciphertext);
    }

    private byte[] doFinal(int mode, byte[] data) {
        checkInit();

        try {
            cipher.init(mode, secretKey, ivParam);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getID() {
        return ICipher.AES_CBC;
    }
}