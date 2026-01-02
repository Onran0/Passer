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

public final class SecuredLong {

    private byte[] data;

    public SecuredLong() { }

    public SecuredLong(long[] plaintext) {
        this.setPlainData(plaintext);
    }

    public void setPlainData(long[] plaintext) {
        this.data = RuntimeSecurity.encrypt(
                getClass().getName(),
                new byte[] {
                        (byte) (plaintext[0] & 0xFF),
                        (byte) ((plaintext[0] >> 8) & 0xFF),
                        (byte) ((plaintext[0] >> 16) & 0xFF),
                        (byte) ((plaintext[0] >> 24) & 0xFF),
                        (byte) ((plaintext[0] >> 32) & 0xFF),
                        (byte) ((plaintext[0] >> 40) & 0xFF),
                        (byte) ((plaintext[0] >> 48) & 0xFF),
                        (byte) ((plaintext[0] >> 56) & 0xFF),
                }
        );

        RuntimeSecurity.clear(plaintext);
    }

    public byte[] getEncryptedData() {
        return data;
    }

    public long[] getDecryptedData() {
        byte[] plaintext = RuntimeSecurity.decrypt(getClass().getName(), data);

        return new long[] {
                (plaintext[0] & 0xFFL) |
                ((plaintext[1] & 0xFFL) << 8) |
                ((plaintext[2] & 0xFFL) << 16) |
                ((plaintext[3] & 0xFFL) << 24) |
                ((plaintext[4] & 0xFFL) << 32) |
                ((plaintext[5] & 0xFFL) << 40) |
                ((plaintext[6] & 0xFFL) << 48) |
                ((plaintext[7] & 0xFFL) << 56)
        };
    }
}