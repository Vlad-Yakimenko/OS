package ua.knu.io.disk;

import java.io.File;
import java.io.RandomAccessFile;

// FileDisk use file to save FS 
public class FileDisk implements Disk {
    // Size of block in bytes
    private int blockSize;

    // Number of blocks in disks 
    private int blockNumber;

    private RandomAccessFile diskFile;

    public FileDisk(String filename, int blockSize, int blockNumber) {
        this.blockNumber = blockNumber;
        this.blockSize = blockSize;

        try {
            diskFile = new RandomAccessFile(new File(filename), "rw");
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    @Override
    public byte[] ReadBlock(int blockID) {
        try {
            byte[] block= new byte[blockSize];

            diskFile.seek(blockID * blockSize);
            diskFile.read(block);

            return block;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void WriteBlock(byte[] block, int blockID) {
        try {
            diskFile.seek(blockID * blockSize);
            diskFile.write(block);

        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public void Init() {
        try {
            diskFile.seek(0);
            byte[] out = new byte[blockSize];
            
            for (int blockID = 0; blockID < blockNumber; blockID++) {
                WriteBlock(out, blockID);
            }

        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    @Override
    public int BlockSize() {
        return blockSize;
    }

    @Override
    public int BlockNumber() {
        return blockNumber;
    }
}