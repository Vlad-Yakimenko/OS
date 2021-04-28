package ua.knu.filesystem;

import org.apache.commons.lang3.tuple.Pair;
import java.util.ArrayList;
import ua.knu.errors.*;

public interface IFileManager {
    int open(int filename);
    int close(int filename);
    void create(int filename) throws DirectoryException;
    byte[] read(int id, int count) throws ReadFileException;
    void seek(int id, int pos) throws SeekFileException;
    ArrayList<Pair<?, ?>> directory();
    void write(int id, String str) throws FileException;
    void remove(int filename);
}
