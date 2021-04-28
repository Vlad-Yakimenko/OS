package ua.knu.filesystem;

import org.apache.commons.lang3.tuple.Pair;
import ua.knu.exceptions.DirectoryException;
import ua.knu.exceptions.FileException;
import ua.knu.exceptions.ReadFileException;
import ua.knu.exceptions.SeekFileException;

import java.util.List;

public interface FileManager {
    int open(int filename);
    int close(int filename);
    void create(int filename) throws DirectoryException;
    byte[] read(int id, int count) throws ReadFileException;
    void seek(int id, int pos) throws SeekFileException;
    List<Pair<?, ?>> directory();
    void write(int id, String str) throws FileException;
    void remove(int filename);
}
