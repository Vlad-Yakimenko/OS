package ua.knu.filesystem;

import lombok.Data;

@Data
public class FileMetadata {
    int filename;
    int descriptorID;
    int directoryEntryID;
    int size;
    
    public FileMetadata(int fn, int di, int ei, int s) {
        filename = fn;
        descriptorID = di;
        directoryEntryID = ei;
        size = s;
    }
    
    @Override
    public int hashCode() {
        return filename;
    }
}
