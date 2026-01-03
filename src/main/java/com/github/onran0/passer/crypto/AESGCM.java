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
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;

public final class AESGCM implements ISymmetricCipher {

    private final static String AES_CIPHER_ID = "AES";
    private final static String AES_GCM_CIPHER_ID = "AES/GCM/NoPadding";

    private final Cipher cipher;

    public AESGCM() {
        try {
            this.cipher = Cipher.getInstance(AES_GCM_CIPHER_ID);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getIVSizeInBytes() {
        return 12;
    }

    @Override
    public int[] getSupportedKeySizes() {
        return new int[] {128, 192, 256};
    }

    @Override
    public byte[] encrypt(byte[] plaintext, byte[] key, byte[] iv) throws GeneralSecurityException {
        return doFinal(Cipher.ENCRYPT_MODE, plaintext, key, iv);
    }

    @Override
    public byte[] decrypt(byte[] ciphertext, byte[] key, byte[] iv) throws GeneralSecurityException {
        return doFinal(Cipher.DECRYPT_MODE, ciphertext, key, iv);
    }

    private byte[] doFinal(int mode, byte[] data, byte[] key, byte[] iv) throws GeneralSecurityException {
        if(iv.length != 12)
            throw new IllegalArgumentException("Invalid IV size");

        int keyBitsCount = key.length * 8;

        if(keyBitsCount != 128 && keyBitsCount != 192 && keyBitsCount != 256)
            throw new IllegalArgumentException("Invalid key size");

        SecretKey secretKey = new SecretKeySpec(key, AES_CIPHER_ID);;
        GCMParameterSpec gcmParam = new GCMParameterSpec(128, iv);

        cipher.init(mode, secretKey, gcmParam);
        return cipher.doFinal(data);
    }

    @Override
    public String getID() {
        return ICipher.AES_GCM;
    }
}