package ua.knu.filesystem;

import lombok.Data;

@Data
public class FileMetadata {
    int filename;
    int descriptorID;
    int directoryEntryID;
    int size;
    
    public FileMetadata(int filename, int descriptorID, int directoryEntryID, int size) {
        this.filename = filename;
        this.descriptorID = descriptorID;
        this.directoryEntryID = directoryEntryID;
        this.size = size;
    }
    
    @Override
    public int hashCode() {
        return filename;
    }
}
