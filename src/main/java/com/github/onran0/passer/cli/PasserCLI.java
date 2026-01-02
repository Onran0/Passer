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