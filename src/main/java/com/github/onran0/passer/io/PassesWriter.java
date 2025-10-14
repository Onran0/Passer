package com.github.onran0.passer.io;

import com.github.onran0.passer.core.PasswordInfo;
import com.github.onran0.passer.core.Passes;
import com.github.onran0.passer.crypto.*;
import com.github.onran0.passer.security.RuntimeSecurity;
import com.github.onran0.passer.security.SecuredCharArray;
import com.github.onran0.passer.util.Convert;

import static com.github.onran0.passer.core.Passes.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

public final class PassesWriter {

    private static final int MAGIC = 0x49505352;

    private static final int KDF_ITERATIONS = 1_000_000;
    private static final int CIPHER_KEY_SIZE = 256;

    private static final CharsetEncoder UTF_ENCODER = StandardCharsets.UTF_8.newEncoder();

    private final DataOutputStream out;
    private final ISymmetricCipher cipher;
    private final IKDF kdf;

    public PassesWriter(final OutputStream out, final String cipherAlgorithm, final String kdfAlgorithm) {
        this.out = new DataOutputStream(out);
        this.cipher = CryptoFactory.getSymmetricCipherInstance(cipherAlgorithm);
        this.kdf = CryptoFactory.getKDFInstance(kdfAlgorithm);
    }

    public void write(final Passes passes, final SecuredCharArray masterPassword) throws IOException, GeneralSecurityException {
        if(!ICipher.AES_GCM.equals(cipher.getID()))
            throw new UnsupportedOperationException("Unsupported encryption algorithm: " + cipher.getID());

        byte[] iv = new byte[cipher.getIVSizeInBytes()];

        CryptoFactory.getSecureRandom().nextBytes(iv);

        cipher.setIV(iv);

        byte[] salt = new byte[kdf.getSaltLength()];

        CryptoFactory.getSecureRandom().nextBytes(salt);

        kdf.setSalt(salt);
        kdf.setIterations(KDF_ITERATIONS);
        kdf.setOutputLength(CIPHER_KEY_SIZE);

        char[] decryptedMasterPassword = masterPassword.getDecryptedData();

        byte[] key = kdf.getDerivedKey(decryptedMasterPassword);

        RuntimeSecurity.clear(decryptedMasterPassword);

        cipher.setKey(key);

        out.write(SharedConstants.MAGIC);
        out.writeShort((short) V_0);
        out.writeUTF(kdf.getID());
        out.write(kdf.getSalt());
        out.writeInt(kdf.getIterations());

        out.writeUTF(cipher.getID());
        out.writeShort((short) cipher.getKey().length);
        out.write(cipher.getIV());

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        final DataOutputStream encryptedOutputData = new DataOutputStream(baos);

        encryptedOutputData.writeInt(passes.getPasses().size());

        for(PasswordInfo passwordInfo : passes.getPasses()) {
            char[] captionChars = passwordInfo.getCaption();

            ByteBuffer captionBuf = UTF_ENCODER.encode(CharBuffer.wrap(captionChars));

            RuntimeSecurity.clear(captionChars);

            byte[] captionBytes = new byte[captionBuf.remaining()];
            captionBuf.get(captionBytes);

            RuntimeSecurity.clear(captionBuf);

            int[] passwordType = passwordInfo.getType();
            byte[] password = passwordInfo.getPassword();
            long[] creationTime = passwordInfo.getCreationTime();
            long[] modificationTime = passwordInfo.getModificationTime();

            encryptedOutputData.writeShort((short) captionBytes.length);
            encryptedOutputData.write(captionBytes);
            encryptedOutputData.writeByte(passwordType[0]);
            encryptedOutputData.writeInt(password.length);
            encryptedOutputData.write(password);
            encryptedOutputData.writeLong(creationTime[0]);
            encryptedOutputData.writeLong(modificationTime[0]);

            RuntimeSecurity.clear(captionBytes);
            RuntimeSecurity.clear(passwordType);
            RuntimeSecurity.clear(password);
            RuntimeSecurity.clear(creationTime);
            RuntimeSecurity.clear(modificationTime);
        }

        encryptedOutputData.close();

        byte[] plaintext = baos.toByteArray();

        byte[] ciphertext = cipher.encrypt(plaintext);

        RuntimeSecurity.clear(plaintext);
        RuntimeSecurity.clear(key);

        out.writeInt(ciphertext.length);
        out.write(ciphertext);
    }
}