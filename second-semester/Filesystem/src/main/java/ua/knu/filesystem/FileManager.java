package ua.knu.filesystem;

import org.apache.commons.lang3.tuple.Pair;
import ua.knu.exceptions.FileOperationException;

import java.util.List;

public interface FileManager {

    void create(int filename) throws FileOperationException;

    void remove(int filename) throws FileOperationException;

    int open(int filename) throws FileOperationException;

    int close(int id) throws FileOperationException;

    byte[] read(int id, int count) throws FileOperationException;

    void write(int id, String str) throws FileOperationException;

    void seek(int id, int pos) throws FileOperationException;

    List<Pair<Integer, Integer>> directory();
}
