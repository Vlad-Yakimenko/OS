package ua.knu.cli.command;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import ua.knu.cli.view.View;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class HelpCommand extends Command {

    private View view;

    @Override
    public boolean canProcess(String command) {
        return command.equals("help");
    }

    @Override
    public void process(String command) {
        view.write("commands:");
        view.write("\tcr name");
        view.write("\t\tcreates file with the given name(filename must contain 4 or less symbols)");
        view.write("\trm name");
        view.write("\t\tremoves file with the given name");
        view.write("\top name");
        view.write("\t\topens file by the given name, returns index of an opened file");
        view.write("\tcl 1");
        view.write("\t\tcloses file by the given index");
        view.write("\twr 1 text");
        view.write("\t\twrites given text in a file by the index of an opened file");
        view.write("\twrs 1 a 10");
        view.write("\t\twrites given symbol n times in a file by the index of an opened file(first parameter is index, second - symbol to be written and third - times)");
        view.write("\trd 1 10");
        view.write("\t\treads the given amount of bytes from an opened file(first parameter is index, second - amount of bytes)");
        view.write("\tsk 1 0");
        view.write("\t\tmoves cursor inside an opened file to the given position(first parameter is index, second - new position)");
        view.write("\tdr");
        view.write("\t\tdisplays all files in the system with its length");
        view.write("\tex");
        view.write("\t\tfor exit");
    }

    @Override
    public String getCommandSample() {
        return "help";
    }
}
