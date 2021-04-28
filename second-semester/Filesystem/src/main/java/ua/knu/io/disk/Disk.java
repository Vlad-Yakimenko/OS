package ua.knu.io.disk;

// Disk interface
public interface Disk {
    byte[] readBlock(int blockID); // readBlock read block with id blockID

    void writeBlock(byte[] block, int blockID); // writeBlock write block with id blockID

    int getBlockSize();

    int getBlockNumber();
}
