package ua.knu.cli;

import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import ua.knu.cli.command.*;
import ua.knu.cli.view.View;
import ua.knu.exceptions.ExitException;
import ua.knu.filesystem.FileManager;
import ua.knu.filesystem.FileManagerImpl;
import ua.knu.io.disk.DiskInitializer;

import java.util.Arrays;
import java.util.List;

@FieldDefaults(makeFinal = true)
public class CliHandler implements Runnable {

    private View view;
    private List<Command> commands;

    public CliHandler(View view) {
        FileManager fileManager = new FileManagerImpl(DiskInitializer.initialize());
        this.view = view;
        this.commands = Arrays.asList(
                new InitCommand(view),
                new CreateCommand(fileManager, view),
                new OpenCommand(fileManager, view),
                new WriteCommand(fileManager, view),
                new ReadCommand(fileManager, view),
                new SeekCommand(fileManager, view),
                new DirectoryCommand(fileManager, view),
                new HelpCommand(view),
                new ExitCommand()
        );
    }

    @Override
    public void run() {
        try {
            handle();
        } catch (ExitException e) {
            view.write("goodbye!");
        }
    }

    @SuppressWarnings({"java:S2189", "InfiniteLoopStatement"})
    @SneakyThrows
    private void handle() {
        view.write("hello!");

        while (true) {
            view.write("enter command (or help if you need):");

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
        }
    }

    private void printError(Exception e) {
        StringBuilder message = new StringBuilder(e.getMessage() == null ? StringUtils.EMPTY : e.getMessage());

        if (e.getCause() != null) {
            message.append(" ").append(e.getCause().getMessage());
        }

        view.write("something went wrong: " + message.toString().toLowerCase());
        view.write("try again.");
    }
}
