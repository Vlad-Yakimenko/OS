package ua.knu;

import ua.knu.cli.CliHandler;
import ua.knu.cli.view.ConsoleView;
import ua.knu.cli.view.FileView;
import ua.knu.exceptions.FileOperationException;

public class Main {
    public static void main(String[] args) throws FileOperationException {
        new CliHandler(new ConsoleView()).run();
    //    new CliHandler(new FileView("src/main/resources/testcase.txt", "src/main/resources/output.txt")).run();
    }
}
