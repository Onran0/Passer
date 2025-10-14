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
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public final class PassesReader {

    private static final CharsetDecoder UTF_DECODER = StandardCharsets.UTF_8.newDecoder();
    private final DataInputStream in;

    public PassesReader(InputStream in) {
        this.in = new DataInputStream(in);
    }

    public Passes read(final SecuredCharArray masterPassword) throws IOException, GeneralSecurityException {
        in.readNBytes(SharedConstants.MAGIC.length);

        final int version = in.readUnsignedShort();

        if(version != V_0)
            throw new UnsupportedOperationException("Unsupported passes file version: " + version);

        final String kdfAlgorithm = in.readUTF();

        final IKDF kdf = CryptoFactory.getKDFInstance(kdfAlgorithm);

        if(kdf == null)
            throw new UnsupportedOperationException("Unsupported KDF algorithm: " + kdfAlgorithm);

        byte[] salt = in.readNBytes(kdf.getSaltLength());

        kdf.setSalt(salt);
        kdf.setIterations(in.readInt());

        final String encryptionAlgorithm = in.readUTF();

        if(!ICipher.AES_GCM.equals(encryptionAlgorithm))
            throw new UnsupportedOperationException("Unsupported encryption algorithm: " + encryptionAlgorithm);

        int keySize = in.readUnsignedShort();

        kdf.setOutputLength(keySize * 8);

        final ISymmetricCipher cipher = CryptoFactory.getSymmetricCipherInstance(encryptionAlgorithm);

        assert cipher != null;

        char[] decryptedMasterPassword = masterPassword.getDecryptedData();

        byte[] key = kdf.getDerivedKey(decryptedMasterPassword);

        RuntimeSecurity.clear(decryptedMasterPassword);

        cipher.setKey(key);
        cipher.setIV(in.readNBytes(cipher.getIVSizeInBytes()));

        final DataInputStream decryptedDataInput = new DataInputStream(new ByteArrayInputStream(
                cipher.decrypt(in.readNBytes(in.readInt()))
        ));

        RuntimeSecurity.clear(key);

        final List<PasswordInfo> passwordInfos = new ArrayList<>();

        int pairsCount = decryptedDataInput.readInt();

        for (int i = 0; i < pairsCount; i++) {
            byte[] captionBytes = decryptedDataInput.readNBytes(decryptedDataInput.readUnsignedShort());

            CharBuffer captionBuffer = UTF_DECODER.decode(ByteBuffer.wrap(captionBytes));

            RuntimeSecurity.clear(captionBytes);

            char[] caption = new char[captionBuffer.remaining()];
            captionBuffer.get(caption);

            RuntimeSecurity.clear(captionBuffer);

            int[] passwordType = { decryptedDataInput.readUnsignedByte() };
            byte[] password = decryptedDataInput.readNBytes(decryptedDataInput.readInt());
            long[] creationTime = { decryptedDataInput.readLong() };
            long[] lastUpdateTime = { decryptedDataInput.readLong() };

            passwordInfos.add(new PasswordInfo(
                caption, passwordType, password, creationTime, lastUpdateTime
            ));
        }

        return new Passes(passwordInfos);
    }
}