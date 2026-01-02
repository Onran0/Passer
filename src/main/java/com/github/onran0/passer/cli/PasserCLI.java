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

package com.github.onran0.passer.cli;

import com.github.onran0.passer.cli.commands.*;
import com.github.onran0.passer.cli.commands.core.CommandsExecutor;
import com.github.onran0.passer.cli.commands.core.UsageCommand;

import com.github.onran0.passer.core.PasserCore;

import java.io.Console;
import java.io.PrintWriter;

import static com.github.onran0.passer.cli.Colors.*;

public class PasserCLI extends PasserCore {

    private final Console console = System.console();
    private final PrintWriter out = console.writer();

    private final CommandsExecutor executor = new CommandsExecutor(this);

    private PasserCLI() {
        executor.setOutput(out);

        executor.addCommand("usage", new UsageCommand(executor));

        executor.addCommand("help", new HelpCommand());
        executor.addCommand("version", new VersionCommand());
        executor.addCommand("make", new MakeCommand());
        executor.addCommand("open", new OpenCommand());
        executor.addCommand("list", new ListCommand());
        executor.addCommand("info", new InfoCommand());
        executor.addCommand("copy", new CopyCommand());
        executor.addCommand("add", new AddCommand());
        executor.addCommand("rem", new RemoveCommand());
        executor.addCommand("mod", new ModifyCommand());
        executor.addCommand("save", new SaveCommand());
        executor.addCommand("close", new CloseCommand());
        executor.addCommand("recent", new RecentCommand());
        executor.addCommand("mred", new EditMasterPassCommand());
    }

    @Override
    public void start() {
        super.start();

        while(true) {
            out.write(YELLOW);
            var in = console.readLine(">> ");
            out.write(RESET);

            if(in.isEmpty()) {
                out.println(RED + "enter the command" + RESET);
                continue;
            }

            if("exit".equals(in.trim())) {
                if(getOpenedFile() == null)
                    break;
                else {
                    out.printf(RED + "to exit you need to close the file%s\n", RESET);
                    continue;
                }
            }

            try {
                executor.execute(in);
            } catch(Exception e) {
                out.print(RED + "failed to execute command: ");
                e.printStackTrace(out);
                out.print(RESET);
            }

            System.gc();
        }

        super.stop();
    }

    public static void main(String[] args) {
        new PasserCLI().start();
    }
}