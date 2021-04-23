package ua.knu.io.disk;

// Disk interface
public interface Disk {

    // readBlock read block with id blockID
    public byte[] readBlock(int blockID);

    // writeBlock write block with id blockID
    public void writeBlock(byte[] block, int blockID);

    public int blockSize();
    public int blockNumber();
}