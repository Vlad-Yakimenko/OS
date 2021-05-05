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
    private List<Command> diskCommands;

    @NonFinal
    private List<Command> utilCommands;

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
            var diskCommandProcessed = false;
            view.write("enter command (or help if you need):");

            String input = view.read();

            for (Command command : diskCommands) {
                try {
                    if (command.canProcess(input)) {
                        diskCommandProcessed = true;
                        if (!DiskInitializer.isInitialized()) {
                            view.write("you must initialize or open disk before work with filesystem");
                            break;
                        }
                        command.process(input);
                        break;
                    }
                } catch (Exception e) {
                    resolvingErrorType(e);
                    break;
                }
            }

            if (diskCommandProcessed) {
                continue;
            }

            for (Command command : utilCommands) {
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
        } else if (e instanceof FileOperationException || e instanceof IllegalArgumentException) {
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
        FileManager fileManager = new FileManagerImpl();

        this.diskCommands = Arrays.asList(
                new CreateCommand(fileManager, view),
                new RemoveCommand(fileManager, view),
                new OpenCommand(fileManager, view),
                new CloseCommand(fileManager, view),
                new WriteTextCommand(fileManager, view),
                new WriteSequenceCommand(fileManager, view),
                new ReadCommand(fileManager, view),
                new SeekCommand(fileManager, view),
                new DirectoryCommand(fileManager, view)
        );

        this.utilCommands = Arrays.asList(
                new InitDiskCommand(fileManager, view),
                new OpenDiskCommand(fileManager, view),
                new CloseDiskCommand(view),
                new HelpCommand(view),
                new ExitCommand()
        );
    }
}
