package ua.knu;

import ua.knu.cli.CliHandler;
import ua.knu.cli.view.ConsoleView;

public class Main {

    public static void main(String[] args) {
        new CliHandler(new ConsoleView()).run();
//        new CliHandler(new FileView("src/main/resources/input.txt", "src/main/resources/output.txt")).run();
    }
}
