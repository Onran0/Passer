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