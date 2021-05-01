package ua.knu.cli.command;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import ua.knu.cli.view.View;
import ua.knu.filesystem.FileManager;
import ua.knu.util.FilenameConverter;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class DirectoryCommand extends Command {

    private FileManager fileManager;
    private View view;

    @Override
    public boolean canProcess(String command) {
        return command.equals("dr");
    }

    @Override
    public void process(String command) {
        val directoryRaw = fileManager.directory();

        val directory = directoryRaw.stream()
                .map(pair -> Pair.of(FilenameConverter.convertToString(pair.getLeft()), pair.getRight()))
                .collect(Collectors.toList());

        view.write(directory.toString());
    }

    @Override
    public String getCommandSample() {
        return "dr";
    }
}
