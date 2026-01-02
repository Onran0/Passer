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

import com.github.onran0.passer.cli.commands.core.NonOptionArgumentsParser;
import com.github.onran0.passer.cli.commands.core.PasswordBasedCommand;
import com.github.onran0.passer.security.RuntimeSecurity;
import com.github.onran0.passer.security.SecuredCharArray;
import com.github.onran0.passer.security.SecurityUtil;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import static com.github.onran0.passer.cli.Colors.RED;
import static com.github.onran0.passer.cli.Colors.RESET;

public class EditMasterPassCommand extends PasswordBasedCommand {

    @Override
    protected boolean openedFileRequired() { return true; }

    @Override
    protected void initializeParser(OptionParser parser, NonOptionArgumentsParser arguments) {
        parser.acceptsAll(Arrays.asList("ohx", "oldhex", "ohex"), "indicates that the old password format is hexadecimal");
        parser.acceptsAll(Arrays.asList("nhx", "newhex", "nhex"), "indicates that the old password format is hexadecimal");
    }

    @Override
    protected void execute(OptionSet options) {
        final char[] oldPassword = requestPassword(options.has("ohex"), "enter old password");
        final char[] newPassword = requestPassword(options.has("nhex"), "enter new password");
        final char[] newPasswordConfirm = requestPassword(options.has("nhex"), "enter new password confirmation");

        if(!SecurityUtil.passwordsAreMatch(newPassword, newPasswordConfirm)) {
            out().println(RED + "new password and its confirm are mismatch" + RESET);

            RuntimeSecurity.clear(oldPassword);
            RuntimeSecurity.clear(newPassword);
        } else {
            var securedOld = new SecuredCharArray(oldPassword);
            var securedNew = new SecuredCharArray(newPassword);

            var core = getCore();

            try {
                core.editMasterPassword(securedOld, securedNew);
                out().println("master password successfully changed");
                out().println("type 'save' command for apply new password");
            } catch(IOException e) {
                out().println(RED + "error" + RESET);
            } catch(GeneralSecurityException e) {
                out().println(RED + "current password and passed old are mismatch" + RESET);
            }
        }

        RuntimeSecurity.clear(newPasswordConfirm);
    }
}