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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class SHA2 implements IHashingAlgorithm {

    private int hashSize = -1;

    @Override
    public String getID() {
        return IHashingAlgorithm.SHA2;
    }

    @Override
    public void setHashSize(int hashSize) {
        if(hashSize != 224 && hashSize != 256 && hashSize != 384 && hashSize != 512)
            throw new IllegalArgumentException("Invalid SHA-2 hash size");

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
            return MessageDigest.getInstance("SHA-" + hashSize).digest(data);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}