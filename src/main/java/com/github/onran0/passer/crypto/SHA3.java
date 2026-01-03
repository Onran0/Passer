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

import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class SHA3 implements IHashingAlgorithm {

    @Override
    public String getID() {
        return IHashingAlgorithm.SHA3;
    }

    @Override
    public int[] getSupportedKeySizes() {
        return new int[] {224,256,384,512};
    }

    @Override
    public void digest(byte[] data, byte[] digest) {
        int hashSize = digest.length * 8;

        if(hashSize != 224 && hashSize != 256 && hashSize != 384 && hashSize != 512)
            throw new IllegalArgumentException("Invalid SHA-3 hash size");

        try {
            var md =  MessageDigest.getInstance("SHA3-" + hashSize);

            md.update(data);
            md.digest(digest, 0, digest.length);
        } catch (NoSuchAlgorithmException | DigestException e) {
            throw new RuntimeException(e);
        }
    }
}