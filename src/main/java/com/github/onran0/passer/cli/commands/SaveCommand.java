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

package com.github.onran0.passer.cli.commands;

import com.github.onran0.passer.cli.commands.core.Command;
import com.github.onran0.passer.cli.commands.core.NonOptionArgumentsParser;
import com.github.onran0.passer.crypto.CryptoFactory;
import com.github.onran0.passer.crypto.ICipher;
import com.github.onran0.passer.crypto.IKDF;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import static com.github.onran0.passer.cli.Colors.RED;
import static com.github.onran0.passer.cli.Colors.RESET;

public class SaveCommand extends Command {

    @Override
    protected boolean openedFileRequired() { return true; }

    @Override
    protected void initializeParser(OptionParser parser, NonOptionArgumentsParser arguments) {
        parser.acceptsAll(Arrays.asList("e", "enc", "encryption"), "Encryption algorithm ( \"aes_gcm\" )")
                .withRequiredArg()
                .ofType(String.class);

        parser.accepts("kdf", "KDF algorithm ( \"pbkdf2\" )")
                .withRequiredArg()
                .ofType(String.class);
    }

    private String combAlgoName(String algo) {
        return algo.replace('_', '/').toUpperCase();
    }

    @Override
    protected void execute(OptionSet options) {
        try {
            String encryptionAlgorithm = ICipher.AES_GCM;
            String kdfAlgorithm = IKDF.PBKDF2;

            if (options.has("encryption")) {
                encryptionAlgorithm = combAlgoName((String) options.valueOf("encryption"));

                if(CryptoFactory.getSymmetricCipherInstance(encryptionAlgorithm) == null) {
                    invalidUsage("undefined encryption algorithm");
                    return;
                }
            }

            if (options.has("kdf")) {
                kdfAlgorithm = combAlgoName((String) options.valueOf("kdf"));

                if(CryptoFactory.getKDFInstance(kdfAlgorithm) == null) {
                    invalidUsage("undefined kdf algorithm");
                    return;
                }
            }

            getCore().saveFile(encryptionAlgorithm, kdfAlgorithm);

            out().println("file successfully saved");
        } catch(IOException | GeneralSecurityException e) {
            out().print(RED + "failed to save file: ");
            e.printStackTrace(out());
            out().print(RESET);
        }
    }
}