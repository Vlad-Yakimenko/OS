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
        String message = parameters[2];

        fileManager.write(fileId, message);
        view.write(String.format("%s bytes written", message.length()));
    }

    @Override
    public String getCommandSample() {
        return "wr 1 message";
    }
}
