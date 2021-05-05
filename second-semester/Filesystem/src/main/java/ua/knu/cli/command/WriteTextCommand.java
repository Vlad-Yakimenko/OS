package ua.knu.cli.command;

import ua.knu.cli.view.View;
import ua.knu.filesystem.FileManager;

import java.util.Arrays;
import java.util.StringJoiner;

public class WriteTextCommand extends WriteCommand {

    public WriteTextCommand(FileManager fileManager, View view) {
        super(fileManager, view);
    }

    @Override
    public boolean canProcess(String command) {
        return command.startsWith("wr ");
    }

    @Override
    public String getCommandSample() {
        return "wr 1 text";
    }

    @Override
    protected void verifyCorrectParametersAmount(int actualParametersAmount) {
        if (actualParametersAmount < 3) {
            throw new IllegalArgumentException(
                    String.format("Incorrect amount of parameters, expected more than 3, but actual %s", actualParametersAmount));
        }
    }

    protected String getMessage(String[] parameters) {
        String[] messageArray = new String[parameters.length - 2];

        System.arraycopy(parameters, 2, messageArray, 0, messageArray.length);

        StringJoiner stringJoiner = new StringJoiner(" ");

        Arrays.stream(messageArray).forEach(stringJoiner::add);

        return stringJoiner.toString();
    }
}
