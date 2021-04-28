package ua.knu.cli.command;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import ua.knu.cli.view.View;
import ua.knu.io.disk.DiskInitializer;
import ua.knu.util.Constants;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class InitCommand extends Command {

    private View view;

    @Override
    public boolean canProcess(String command) {
        return command.equals("in");
    }

    @Override
    public void process(String command) {
        val parameters = command.split(Constants.COMMAND_SEPARATOR);

        verifyCorrectParametersAmount(parameters.length);

        if (!DiskInitializer.isInitialized) {
            view.write("disk initialized");
        } else {
            view.write("disk restored");
        }
    }

    @Override
    public String getCommandSample() {
        return "in";
    }
}
