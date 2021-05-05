package ua.knu.cli.command;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import ua.knu.cli.view.View;
import ua.knu.filesystem.FileManager;
import ua.knu.filesystem.FileManagerImpl;
import ua.knu.io.disk.DiskInitializer;
import ua.knu.util.Constants;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class InitDiskCommand extends Command {

    private FileManager fileManager;
    private View view;

    @Override
    public boolean canProcess(String command) {
        return command.startsWith("ind ");
    }

    @Override
    public void process(String command) {
        val parameters = command.split(Constants.COMMAND_SEPARATOR);

        verifyCorrectParametersAmount(parameters.length);

        ((FileManagerImpl) fileManager).init(DiskInitializer.initialize(parameters[1]));
        view.write("disk initialized");
    }

    @Override
    public String getCommandSample() {
        return "ind name";
    }
}
