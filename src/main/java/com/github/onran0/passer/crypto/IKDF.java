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

package com.github.onran0.passer.crypto;

import java.util.List;

public interface IKDF {
    String PBKDF2 = "PBKDF2";

    List<String> ALGORITHMS = List.of(PBKDF2);

    String getID();

    void setSalt(byte[] salt);

    byte[] getSalt();

    void setIterations(int iterations);

    int getIterations();

    void setOutputLength(int outputLength);

    int getOutputLength();

    int getSaltLength();

    byte[] getDerivedKey(char[] material);
}