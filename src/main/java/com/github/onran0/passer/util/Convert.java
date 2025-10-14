package com.github.onran0.passer.util;

import java.nio.charset.StandardCharsets;

public final class Convert {

    public static byte[] getBinaryFromHex(String hex) {
        return getBinaryFromHex(hex.toCharArray());
    }

    public static byte[] getBinaryFromHex(char[] hex) {
        if(hex.length % 2 != 0)
            throw new IllegalArgumentException("hex length must be even");

        byte[] bytes = new byte[hex.length / 2];

        for(int i = 0; i < bytes.length; i++)
            bytes[i] = (byte) Integer.parseInt("" + hex[i * 2] + hex[i * 2 + 1], 16);

        return bytes;
    }

    public static String binaryToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);

        for (byte b : bytes)
            sb.append(String.format("%02x", b));

        return sb.toString();
    }

    public static byte[] getUTF8Bytes(String s) {
        return s.getBytes(StandardCharsets.UTF_8);
    }
}