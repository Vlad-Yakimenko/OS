package ua.knu.cli.command;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.val;
import ua.knu.cli.view.View;
import ua.knu.filesystem.FileManager;
import ua.knu.util.Constants;
import ua.knu.util.FilenameConverter;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class CreateCommand extends Command {

    private FileManager fileManager;
    private View view;

    @Override
    public boolean canProcess(String command) {
        return command.startsWith("cr ");
    }

    @Override
    @SneakyThrows
    public void process(String command) {
        val parameters = command.split(Constants.COMMAND_SEPARATOR);

        verifyCorrectParametersAmount(parameters.length);

        String filename = parameters[1];

        if (filename.length() > 4) {
            throw new IllegalArgumentException("filename length must contain 4 or less symbols");
        }

        // TODO fail already exists exception
        fileManager.create(FilenameConverter.convertToInt(filename));
        view.write(String.format("file %s created", filename));
    }

    @Override
    public String getCommandSample() {
        return "cr filename";
    }
}
