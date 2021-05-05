package ua.knu.cli.command;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.val;
import ua.knu.cli.view.View;
import ua.knu.filesystem.FileManager;
import ua.knu.util.Constants;

import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class ReadCommand extends Command {

    private FileManager fileManager;
    private View view;

    @Override
    public boolean canProcess(String command) {
        return command.startsWith("rd ");
    }

    @Override
    @SneakyThrows
    public void process(String command) {
        val parameters = command.split(Constants.COMMAND_SEPARATOR);

        verifyCorrectParametersAmount(parameters.length);

        int fileId = Integer.parseInt(parameters[1]);
        int amountOfBytes = Integer.parseInt(parameters[2]);

        byte[] result = fileManager.read(fileId, amountOfBytes);

        view.write(String.format("%s bytes read: %s", result.length, new String(result, StandardCharsets.UTF_8)));
    }

    @Override
    public String getCommandSample() {
        return "rd 1 10";
    }
}
