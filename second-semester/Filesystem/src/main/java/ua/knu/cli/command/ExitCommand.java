package ua.knu.cli.command;

import ua.knu.exceptions.ExitException;

public class ExitCommand implements Command {

    @Override
    public boolean canProcess(String command) {
        return command.equals("ex");
    }

    @Override
    public void process(String command) {
        throw new ExitException();
    }

    @Override
    public String getCommandSample() {
        return "ex";
    }
}
