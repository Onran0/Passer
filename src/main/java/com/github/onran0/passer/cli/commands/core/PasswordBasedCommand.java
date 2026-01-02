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

package com.github.onran0.passer.cli.commands.core;

import com.github.onran0.passer.core.PasswordType;
import com.github.onran0.passer.crypto.CryptoFactory;
import com.github.onran0.passer.util.Convert;

import java.nio.charset.StandardCharsets;

public abstract class PasswordBasedCommand extends Command {

    private static final String PRINTABLE_ASCII = "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";

    protected char[] requestPassword(boolean hex) {
        return requestPassword(hex, "enter password");
    }

    protected char[] requestPassword(boolean hex, String text) {
        if(!hex)
            return System.console().readPassword(text + ": ");
        else
            return System.console().readPassword(text + " in hex: ");
    }

    protected byte[] generatePassword(int[] type) {
        if (type[0] == PasswordType.TEXT.getID()) {
            StringBuilder randomPassword = new StringBuilder();

            for (int i = 0; i < 16; i++) {
                randomPassword.append(PRINTABLE_ASCII.charAt(
                        CryptoFactory.getSecureRandom().nextInt(0, PRINTABLE_ASCII.length())
                ));
            }

            return randomPassword.toString().getBytes(StandardCharsets.UTF_8);
        } else if(type[0] == PasswordType.BINARY.getID()) {
            byte[] password = new byte[16];

            CryptoFactory.getSecureRandom().nextBytes(password);

            return password;
        } else throw new RuntimeException("Undefined password type: " + type[0]);
    }

    protected byte[] extractBytesFromInputPassword(char[] passwordInput, int[] type) {
        if(type[0] == PasswordType.BINARY.getID()) {
            try {
                return Convert.getBinaryFromHex(passwordInput);
            } catch(NumberFormatException ignored) {
                return null;
            }
        } else if(type[0] == PasswordType.TEXT.getID()) {
            return Convert.getUTF8Bytes(passwordInput);
        } else throw new RuntimeException("Undefined password type: " + type[0]);
    }
}