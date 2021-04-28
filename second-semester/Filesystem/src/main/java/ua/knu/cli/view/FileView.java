package ua.knu.cli.view;

import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.Charset;

@FieldDefaults(makeFinal = true)
public class FileView implements View {

    private File outputFile;
    private BufferedReader reader;

    @SneakyThrows
    public FileView(String inputFile, String outputFile) {
        this.outputFile = FileUtils.getFile(outputFile);

        File file = FileUtils.getFile(inputFile);
        this.reader = new BufferedReader(new FileReader(file));
    }

    @Override
    @SneakyThrows
    public void write(String message) {
        FileUtils.writeStringToFile(outputFile, message, Charset.defaultCharset(), true);
    }

    @Override
    @SneakyThrows
    public String read() {
        return reader.readLine();
    }
}
