package ua.knu.cli;

import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.apache.commons.lang3.StringUtils;
import ua.knu.cli.command.*;
import ua.knu.cli.view.View;
import ua.knu.exceptions.ExitException;
import ua.knu.exceptions.FileOperationException;
import ua.knu.filesystem.FileManager;
import ua.knu.filesystem.FileManagerImpl;
import ua.knu.io.disk.DiskInitializer;

import java.util.Arrays;
import java.util.List;

@FieldDefaults(makeFinal = true)
public class CliHandler implements Runnable {

    private View view;

    @NonFinal
    private List<Command> commands;

    public CliHandler(View view) {
        this.view = view;
        init();
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
                    resolvingErrorType(e);
                    break;
                }
            }
        }
    }

    private void resolvingErrorType(Exception e) throws ExitException {
        if (e instanceof ExitException) {
            throw ((ExitException) e);
        } else if (e instanceof FileOperationException || e instanceof NumberFormatException) {
            printError(e, "something went wrong: ", "try again.");
        } else {
            init();
            printError(e, "something went especially wrong: ", "restoring disk...");
        }
    }

    private void printError(Exception exception, String explanation, String comment) {
        StringBuilder message = new StringBuilder(exception.getMessage() == null ? StringUtils.EMPTY : exception.getMessage());

        if (exception.getCause() != null) {
            message.append(" ").append(exception.getCause().getMessage());
        }

        view.write(explanation + message.toString().toLowerCase());
        view.write(comment);
    }

    private void init() {
        FileManager fileManager = new FileManagerImpl(DiskInitializer.initialize());
        this.commands = Arrays.asList(
                new InitCommand(view),
                new CreateCommand(fileManager, view),
                new RemoveCommand(fileManager, view),
                new OpenCommand(fileManager, view),
                new CloseCommand(fileManager, view),
                new WriteCommand(fileManager, view),
                new ReadCommand(fileManager, view),
                new SeekCommand(fileManager, view),
                new DirectoryCommand(fileManager, view),
                new HelpCommand(view),
                new ExitCommand()
        );
    }
}
