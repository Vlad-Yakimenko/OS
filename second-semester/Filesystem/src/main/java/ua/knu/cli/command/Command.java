package ua.knu.cli.command;

import ua.knu.util.Constants;

public interface Command {

    boolean canProcess(String command);

    void process(String command);

    String getCommandSample();

    default int getCorrectParametersAmount() {
        return getCommandSample().split(Constants.COMMAND_SEPARATOR).length;
    }
}
