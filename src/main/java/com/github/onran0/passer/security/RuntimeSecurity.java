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

package com.github.onran0.passer.security;

import com.github.onran0.passer.crypto.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import java.nio.charset.StandardCharsets;

import java.security.GeneralSecurityException;

import java.util.Arrays;

public final class RuntimeSecurity {

    private static final int KEY_LENGTH = 32;
    private static final boolean PUT_KEY_TO_CIPHERTEXT = false;
    private static final byte[] KEY;
    private static final ISymmetricCipher CIPHER = new AESGCM();
    private static final IHashingAlgorithm HASHING_ALGORITHM = new SHA2();

    static {
        if (PUT_KEY_TO_CIPHERTEXT)
            KEY = null;
        else {
            KEY = new byte[KEY_LENGTH];
            CryptoFactory.getSecureRandom().nextBytes(KEY);
        }

        HASHING_ALGORITHM.setHashSize(KEY_LENGTH * 8);
    }

    private static byte[] xor(byte[] a, byte[] b) {
        byte[] min = a.length < b.length ? a : b;

        byte[] res = (a.length >= b.length ? a : b).clone();

        for(int i = 0; i < min.length; i++)
            res[i] = (byte) (res[i] ^ min[i]);

        return res;
    }

    public static void clear(Object o) {
        if (o instanceof byte[] ba)
            Arrays.fill(ba, (byte) 0);
        else if (o instanceof char[] ca)
            Arrays.fill(ca,'\0');
        else if (o instanceof int[] ia)
            Arrays.fill(ia, 0);
        else if (o instanceof long[] la)
            Arrays.fill(la, 0);
        else if (o instanceof boolean[] ba)
            Arrays.fill(ba, false);
        else if (o instanceof ByteBuffer bb) {
            bb.clear();

            for(int i = 0;i < bb.capacity();i++)
                bb.put((byte)0);
        } else if (o instanceof CharBuffer cb) {
            cb.clear();

            for(int i = 0;i < cb.capacity();i++)
                cb.put('\0');
        } else throw new RuntimeException("Failed to clear object");
    }

    public static byte[] encrypt(String additionalMaterial, byte[] plaintext) {
        ByteArrayOutputStream result = new ByteArrayOutputStream();

        byte[] key = PUT_KEY_TO_CIPHERTEXT ? new byte[KEY_LENGTH] : KEY;
        byte[] iv = new byte[CIPHER.getIVSizeInBytes()];

        if (PUT_KEY_TO_CIPHERTEXT) CryptoFactory.getSecureRandom().nextBytes(key);
        CryptoFactory.getSecureRandom().nextBytes(iv);

        try {
            if (PUT_KEY_TO_CIPHERTEXT) result.write(key);

            result.write(iv);

            CIPHER.setKey(xor(
                    key,
                    HASHING_ALGORITHM.digest(additionalMaterial.getBytes(StandardCharsets.UTF_8))
            ));
            CIPHER.setIV(iv);

            result.write(CIPHER.encrypt(plaintext));
        } catch(IOException | GeneralSecurityException e) {
            throw new RuntimeException(e);
        }

        if(PUT_KEY_TO_CIPHERTEXT) clear(key);
        clear(CIPHER.getKey());
        clear(CIPHER.getIV());

        return result.toByteArray();
    }

    public static byte[] decrypt(String additionalMaterial, byte[] ciphertext) {
        int keyLength = PUT_KEY_TO_CIPHERTEXT ? KEY_LENGTH : 0;

        byte[] key = PUT_KEY_TO_CIPHERTEXT ? Arrays.copyOfRange(ciphertext, 0, keyLength) : KEY;
        byte[] iv = Arrays.copyOfRange(ciphertext, keyLength, keyLength + CIPHER.getIVSizeInBytes());
        ciphertext = Arrays.copyOfRange(ciphertext, keyLength + CIPHER.getIVSizeInBytes(), ciphertext.length);

        CIPHER.setKey(xor(
                key,
                HASHING_ALGORITHM.digest(additionalMaterial.getBytes(StandardCharsets.UTF_8))
        ));
        CIPHER.setIV(iv);

        byte[] plaintext;

        try {
            plaintext = CIPHER.decrypt(ciphertext);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }

        if(PUT_KEY_TO_CIPHERTEXT) clear(key);
        clear(CIPHER.getKey());
        clear(CIPHER.getIV());

        return plaintext;
    }
}