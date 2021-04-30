package ua.knu.filesystem.oft;

import ua.knu.elements.Descriptor;
import ua.knu.exceptions.FileOperationException;
import ua.knu.io.disk.Disk;

// OpenFileTable (OFT) represents a table of the open files
// @note Directory must always be open
// @note You should modify this class, and/or its interface
public interface OpenFileTable {
    // Open file, must create OFT entry for open file
    // return -1, when cannot open file for some reasons
    int open(int filename) throws FileOperationException;

    // Close file, must free OFT entry
    // @arg id ID of open file
    // return whatever, idk
    int close(int id) throws FileOperationException;

    // Return file Descriptor of open file
    // @arg id ID of open file
    Descriptor getDescriptorByID(int id);

    // Save file Descriptor of open file
    // @arg id ID of open file
    void setDescriptorByID(int id, Descriptor desc);

    // Return number of blocks in file Descriptor
    // @note in this project always returns 3
    int getMaxDescriptorBlockNumber(int id);

    // Load block with index <block> into cache of open file with id <id>
    // return loaded block
    // @note block can be 0-2
    byte[] loadBlock(int id, int block);

    // Store block of open file with id <id> into disk
    void storeBlock(int id);

    // Return L-disk
    Disk getDisk();

    // Return open entry by id
    OftEntry getEntryById(int id);

    // Return max number of entries in open file table
    int getMaxNumEntries();

    // Return whether entry with id <id> is open now
    boolean isEmptyEntry(int id);
}
