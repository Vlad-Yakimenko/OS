package ua.knu.cli.command;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import ua.knu.cli.view.View;
import ua.knu.io.disk.DiskInitializer;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class CloseDiskCommand extends Command {

    private View view;

    @Override
    public boolean canProcess(String command) {
        return command.equals("cld");
    }

    @Override
    public void process(String command) {
        DiskInitializer.setInitialized(false);
        view.write("disk closed");
    }

    @Override
    public String getCommandSample() {
        return "cld";
    }
}
