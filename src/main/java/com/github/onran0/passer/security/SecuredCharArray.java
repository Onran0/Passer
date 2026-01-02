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

import java.nio.*;
import java.nio.charset.*;

public final class SecuredCharArray {

    private static final CharsetEncoder UTF_ENCODER = StandardCharsets.UTF_8.newEncoder();
    private static final CharsetDecoder UTF_DECODER = StandardCharsets.UTF_8.newDecoder();
    private byte[] data;

    public SecuredCharArray() {}

    public SecuredCharArray(char[] plaintext) {
        this.setPlainData(plaintext);
    }

    public void setPlainData(char[] plaintext) {
        try {
            ByteBuffer buf = UTF_ENCODER.encode(CharBuffer.wrap(plaintext));

            byte[] data = new byte[buf.remaining()];

            buf.get(data);

            this.data = RuntimeSecurity.encrypt(getClass().getName(), data);

            RuntimeSecurity.clear(data);

            buf.clear();

            RuntimeSecurity.clear(buf);
        } catch(CharacterCodingException e) {
            e.printStackTrace();
        }

        RuntimeSecurity.clear(plaintext);
    }

    public byte[] getEncryptedData() {
        return data;
    }

    public char[] getDecryptedData() {
        byte[] plaintext = RuntimeSecurity.decrypt(getClass().getName(), data);

        char[] chars = null;

        try {
            CharBuffer buf = UTF_DECODER.decode(ByteBuffer.wrap(plaintext));

            chars = new char[buf.remaining()];

            buf.get(chars);

            buf.clear();

            RuntimeSecurity.clear(buf);
        } catch (CharacterCodingException e) {
            e.printStackTrace();
        }

        RuntimeSecurity.clear(plaintext);

        return chars;
    }
}