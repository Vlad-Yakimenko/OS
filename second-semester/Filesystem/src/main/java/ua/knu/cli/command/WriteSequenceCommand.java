package ua.knu.cli.command;

import lombok.val;
import ua.knu.cli.view.View;
import ua.knu.filesystem.FileManager;

import java.util.StringJoiner;
import java.util.stream.IntStream;

public class WriteSequenceCommand extends WriteCommand {

    public WriteSequenceCommand(FileManager fileManager, View view) {
        super(fileManager, view);
    }

    @Override
    public boolean canProcess(String command) {
        return command.startsWith("wrs ");
    }

    @Override
    public String getCommandSample() {
        return "wrs 1 a 10";
    }

    protected String getMessage(String[] parameters) {
        val symbol = parameters[2];

        if (symbol.length() != 1) {
            throw new IllegalArgumentException(String.format("third parameter must by a symbol, but actual is '%s'", symbol));
        }

        StringJoiner stringJoiner = new StringJoiner("");

        IntStream.range(0, Integer.parseInt(parameters[3])).forEach(whatever -> stringJoiner.add(symbol));

        return stringJoiner.toString();
    }
}
