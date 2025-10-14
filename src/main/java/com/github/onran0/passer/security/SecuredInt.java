package com.github.onran0.passer.security;

public final class SecuredInt {

    private byte[] data;

    public SecuredInt() { }

    public SecuredInt(int[] plaintext) {
        this.setPlainData(plaintext);
    }

    public void setPlainData(int[] plaintext) {
        this.data = RuntimeSecurity.encrypt(
                getClass().getName(),
                new byte[] {
                        (byte) (plaintext[0] & 0xFF),
                        (byte) (plaintext[0] >> 8 & 0xFF),
                        (byte) (plaintext[0] >> 16 & 0xFF),
                        (byte) (plaintext[0] >> 24 & 0xFF)
                }
        );

        RuntimeSecurity.clear(plaintext);
    }

    public byte[] getEncryptedData() {
        return data;
    }

    public int[] getDecryptedData() {
        byte[] plaintext = RuntimeSecurity.decrypt(getClass().getName(), data);

        return new int[] {
                (plaintext[0] & 0xFF) |
                ((plaintext[1] & 0xFF) << 8) |
                ((plaintext[2] & 0xFF) << 16) |
                ((plaintext[3] & 0xFF) << 24)
        };
    }
}