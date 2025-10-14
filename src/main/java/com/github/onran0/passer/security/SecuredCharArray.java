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