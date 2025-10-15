package com.github.onran0.passer.security;

public final class SecuredBool {

    private byte[] data;

    public SecuredBool() { }

    public SecuredBool(boolean[] plaintext) {
        this.setPlainData(plaintext);
    }

    public void setPlainData(boolean[] plaintext) {
        this.data = RuntimeSecurity.encrypt(
                getClass().getName(),
                new byte[] {(byte) (plaintext[0] ? 1 : 0)}
        );

        RuntimeSecurity.clear(plaintext);
    }

    public byte[] getEncryptedData() {
        return data;
    }

    public boolean[] getDecryptedData() {
        byte[] plaintext = RuntimeSecurity.decrypt(getClass().getName(), data);

        return new boolean[] { plaintext[0] == 1 };
    }
}