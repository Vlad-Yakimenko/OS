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
public class SeekCommand extends Command {

    private FileManager fileManager;
    private View view;

    @Override
    public boolean canProcess(String command) {
        return command.startsWith("sk ");
    }

    @Override
    @SneakyThrows
    public void process(String command) {
        val parameters = command.split(Constants.COMMAND_SEPARATOR);

        verifyCorrectParametersAmount(parameters.length);

        int fileId = Integer.parseInt(parameters[1]);
        int position = Integer.parseInt(parameters[2]);

        fileManager.seek(fileId, position);
    }

    @Override
    public String getCommandSample() {
        return "sk 1 10";
    }
}
