package com.github.onran0.passer.security;

public final class SecuredByteArray {

    private byte[] data;

    public SecuredByteArray() { }

    public SecuredByteArray(byte[] plaintext) {
        this.setPlainData(plaintext);
    }

    public void setPlainData(byte[] plaintext) {
        this.data = RuntimeSecurity.encrypt(getClass().getName(), plaintext);

        RuntimeSecurity.clear(plaintext);
    }

    public byte[] getEncryptedData() {
        return data;
    }

    public byte[] getDecryptedData() {
        return RuntimeSecurity.decrypt(getClass().getName(), data);
    }
}