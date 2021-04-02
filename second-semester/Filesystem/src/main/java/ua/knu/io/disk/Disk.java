package ua.knu.io.disk;

// Disk interface
public interface Disk {

    // ReadBlock read block with id blockID
    public byte[] ReadBlock(int blockID);

    // WriteBlock write block with id blockID
    public void WriteBlock(byte[] block, int blockID);

    public int BlockSize();
    public int BlockNumber();
}