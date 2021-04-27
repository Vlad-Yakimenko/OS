package ua.knu.io.disk;

// Disk interface
public interface Disk {

    // readBlock read block with id blockID
    byte[] readBlock(int blockID);

    // writeBlock write block with id blockID
    void writeBlock(byte[] block, int blockID);

    int getBlockSize();

    int getBlockNumber();
}
