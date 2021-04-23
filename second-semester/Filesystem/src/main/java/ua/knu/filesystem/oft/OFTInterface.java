package ua.knu.filesystem.oft;

import ua.knu.elements.*;
import ua.knu.io.disk.Disk;

public interface OFTInterface {
    // Open file, must create OFT entry for open file
    // return -1, when cannot open file for some reasons
    public int open(int filename);

    // Close file, must free OFT entry
    // @arg id ID of open file
    // return whatever, idk
    public int close(int id);

    // Return file Descriptor of open file
    // @arg id ID of open file
    public Descriptor getDescriptorByID(int id);

    // Save file Descriptor of open file
    // @arg id ID of open file
    public void setDescriptorByID(int id, Descriptor desc);

    // Return number of blocks in file Descriptor
    // @note in this project always returns 3
    public int getMaxDescriptorBlockNumber(int id);

    // Load block with index <block> into cache of open file with id <id>
    // return loaded block
    // @note block can be 0-2
    public byte[] loadBlock(int id, int block);

    // Stores block of open file with id <id> into disk
    public void storeBlock(int id);

    public Disk getDisk();
}
