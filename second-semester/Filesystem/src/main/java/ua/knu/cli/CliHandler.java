package ua.knu.cli;

import lombok.experimental.FieldDefaults;
import ua.knu.cli.command.Command;
import ua.knu.cli.command.ExitCommand;
import ua.knu.cli.command.HelpCommand;
import ua.knu.cli.view.View;
import ua.knu.exceptions.ExitException;

import java.util.Arrays;
import java.util.List;

@FieldDefaults(makeFinal = true)
public class CliHandler implements Runnable {

    private View view;
    private List<Command> commands;

    public CliHandler(View view) {
        this.view = view;
        this.commands = Arrays.asList(
                new HelpCommand(view),
                new ExitCommand()
        );
    }

    @Override
    public void run() {
        try {
            handle();
        } catch (ExitException e) {
            view.write("Goodbye!");
        }
    }

    @SuppressWarnings({"java:S2189", "InfiniteLoopStatement"})
    private void handle() {
        view.write("Hello!");

        while (true) {
            String input = view.read();

            for (Command command : commands) {
                try {
                    if (command.canProcess(input)) {
                        command.process(input);
                        break;
                    }
                } catch (Exception e) {
                    if (e instanceof ExitException) {
                        throw e;
                    }

                    printError(e);
                    break;
                }
            }

            view.write("Enter command (or help if you need):");
        }
    }

    private void printError(Exception e) {
        StringBuilder message = new StringBuilder(e.getMessage());
        if (e.getCause() != null) {
            message.append(" ").append(e.getCause().getMessage());
        }
        view.write("Failed by cause: " + message);
        view.write("Try again.");
    }
}
