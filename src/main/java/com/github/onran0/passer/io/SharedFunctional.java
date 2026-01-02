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

package com.github.onran0.passer.io;

import com.github.onran0.passer.security.RuntimeSecurity;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.DataOutputStream;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

public final class SharedFunctional {
    public static final byte[] MAGIC = { '.', 'P', 'A', 'S', 'S', 'E', 'R' };

    private static final CharsetDecoder UTF_DECODER = StandardCharsets.UTF_8.newDecoder();
    private static final CharsetEncoder UTF_ENCODER = StandardCharsets.UTF_8.newEncoder();

    public static void writeUTFSecured(DataOutputStream out, char[] str) throws IOException {
        ByteBuffer buf = UTF_ENCODER.encode(CharBuffer.wrap(str));

        RuntimeSecurity.clear(str);

        byte[] bytes = new byte[buf.remaining()];
        buf.get(bytes);

        RuntimeSecurity.clear(buf);

        out.writeShort((short) bytes.length);
        out.write(bytes);

        RuntimeSecurity.clear(bytes);
    }

    public static char[] readUTFSecured(DataInputStream in) throws IOException {
        byte[] bytes = in.readNBytes(in.readUnsignedShort());

        CharBuffer buffer = UTF_DECODER.decode(ByteBuffer.wrap(bytes));

        RuntimeSecurity.clear(bytes);

        char[] utf = new char[buffer.remaining()];
        buffer.get(utf);

        RuntimeSecurity.clear(buffer);

        return utf;
    }
}