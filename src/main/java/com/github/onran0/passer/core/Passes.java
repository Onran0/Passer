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

package com.github.onran0.passer.core;

import java.util.ArrayList;
import java.util.List;

public final class Passes {

    private final List<PasswordInfo> passwordInfos;

    public Passes() {
        this(new ArrayList<>());
    }

    public Passes(final List<PasswordInfo> passwordInfos) {
        this.passwordInfos = passwordInfos;
    }

    public List<PasswordInfo> getPasses() {
        return passwordInfos;
    }
}