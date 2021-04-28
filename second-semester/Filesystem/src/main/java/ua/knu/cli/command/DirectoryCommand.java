package ua.knu.cli.command;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import ua.knu.cli.view.View;
import ua.knu.filesystem.FileManager;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class DirectoryCommand extends Command {

    private FileManager fileManager;
    private View view;

    @Override
    public boolean canProcess(String command) {
        return command.equals("dr");
    }

    @Override
    public void process(String command) {
        view.write(fileManager.directory().toString());
    }

    @Override
    public String getCommandSample() {
        return "dr";
    }
}
