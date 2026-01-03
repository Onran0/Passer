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

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class CryptoFactory {

    private static final SecureRandom secureRandom;
    private static final Map<String, Supplier<ISymmetricCipher>> symmetricCiphers = new HashMap<>();
    private static final Map<String, Supplier<IHashingAlgorithm>> hashingAlgorithms = new HashMap<>();
    private static final Map<String, Supplier<IKDF>> kdfAlgorithms = new HashMap<>();

    static {
        symmetricCiphers.put(ICipher.AES_GCM, AESGCM::new);

        hashingAlgorithms.put(IHashingAlgorithm.SHA2, SHA2::new);
        hashingAlgorithms.put(IHashingAlgorithm.SHA3, SHA3::new);

        kdfAlgorithms.put(IKDF.PBKDF2, PBKDF2::new);

        SecureRandom temp;

        try {
            temp = SecureRandom.getInstanceStrong();
        } catch(NoSuchAlgorithmException e) {
            temp = new SecureRandom();
        }

        secureRandom = temp;
    }

    public static SecureRandom getSecureRandom() {
        return secureRandom;
    }

    public static ISymmetricCipher getSymmetricCipherInstance(String algorithm) {
        if(!symmetricCiphers.containsKey(algorithm))
            return null;

        return symmetricCiphers.get(algorithm).get();
    }

    public static IHashingAlgorithm getHashingAlgorithmInstance(String algorithm) {
        if(!hashingAlgorithms.containsKey(algorithm))
            return null;

        return hashingAlgorithms.get(algorithm).get();
    }

    public static IKDF getKDFInstance(String algorithm) {
        if(!kdfAlgorithms.containsKey(algorithm))
            return null;

        return kdfAlgorithms.get(algorithm).get();
    }
}