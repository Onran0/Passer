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