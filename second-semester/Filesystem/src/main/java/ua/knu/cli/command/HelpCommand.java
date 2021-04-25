package ua.knu.cli.command;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import ua.knu.cli.view.View;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class HelpCommand implements Command {

    private View view;

    @Override
    public boolean canProcess(String command) {
        return command.equals("help");
    }

    @Override
    public void process(String command) {
        view.write("Commands:");
        view.write("\texit");
        view.write("\t\tFor exit");
    }
}
