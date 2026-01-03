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

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PBKDF2 implements IKDF {

    private static final int SALT_LENGTH = 16;

    private static final String PBKDF2_KDF_ID = "PBKDF2WithHmacSHA256";

    private byte[] salt;
    private int iterations = -1;

    private void checkInit() {
        if(
                salt == null || iterations == -1
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
    public int getSaltLength() {
        return SALT_LENGTH;
    }

    @Override
    public void getDerivedKey(byte[] key, char[] material) {
        try {
            PBEKeySpec spec = new PBEKeySpec(material, salt, iterations, key.length * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_KDF_ID);


            byte[] gkey = skf.generateSecret(spec).getEncoded();

            System.arraycopy(gkey, 0, key, 0, gkey.length);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}