package ua.knu.filesystem;

import org.apache.commons.lang3.tuple.Pair;
import ua.knu.exceptions.DirectoryException;
import ua.knu.exceptions.FileException;
import ua.knu.exceptions.ReadFileException;
import ua.knu.exceptions.SeekFileException;

import java.util.List;

public interface FileManager {

    void create(int filename) throws DirectoryException;

    void remove(int filename);

    int open(int filename);

    int close(int filename);

    byte[] read(int id, int count) throws ReadFileException;

    void write(int id, String str) throws FileException;

    void seek(int id, int pos) throws SeekFileException;

    List<Pair<Integer, Integer>> directory();
}
