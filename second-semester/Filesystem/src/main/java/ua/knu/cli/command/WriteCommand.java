package ua.knu.cli.command;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.val;
import ua.knu.cli.view.View;
import ua.knu.filesystem.FileManager;
import ua.knu.util.Constants;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public abstract class WriteCommand extends Command {

    private FileManager fileManager;
    private View view;

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

    protected abstract String getMessage(String[] parameters);
}
