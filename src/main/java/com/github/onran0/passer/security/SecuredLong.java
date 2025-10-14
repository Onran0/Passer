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