package ua.knu.cli.command;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import ua.knu.cli.view.View;
import ua.knu.filesystem.FileManager;
import ua.knu.util.Constants;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class OpenCommand extends Command {

    private FileManager fileManager;
    private View view;

    @Override
    public boolean canProcess(String command) {
        return command.startsWith("op ");
    }

    @Override
    public void process(String command) {
        val parameters = command.split(Constants.COMMAND_SEPARATOR);

        verifyCorrectParametersAmount(parameters.length);

        String filename = parameters[1];

        int index = fileManager.open(Integer.parseInt(filename));

        if (index < 0) {
            view.write("we can not open this file");
        } else {
            view.write(String.format("file %s opened, index=%s", filename, index));
        }
    }

    @Override
    public String getCommandSample() {
        return "op filename";
    }
}
