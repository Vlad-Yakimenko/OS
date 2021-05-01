package ua.knu.cli.command;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.val;
import ua.knu.cli.view.View;
import ua.knu.filesystem.FileManager;
import ua.knu.util.Constants;

import java.util.Arrays;
import java.util.StringJoiner;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class WriteCommand extends Command {

    private FileManager fileManager;
    private View view;

    @Override
    public boolean canProcess(String command) {
        return command.startsWith("wr ");
    }

    @Override
    @SneakyThrows
    public void process(String command) {
        val parameters = command.split(Constants.COMMAND_SEPARATOR);

        verifyCorrectParametersAmount(parameters.length);

        int fileId = Integer.parseInt(parameters[1]);
        String message = getMessage(parameters);

        fileManager.write(fileId, message);
        view.write(String.format("%s bytes written", message.length()));
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

    private String getMessage(String[] parameters) {
        String[] messageArray = new String[parameters.length - 2];

        System.arraycopy(parameters, 2, messageArray, 0, messageArray.length);

        StringJoiner stringJoiner = new StringJoiner(" ");

        Arrays.stream(messageArray).forEach(stringJoiner::add);

        return stringJoiner.toString();
    }
}
