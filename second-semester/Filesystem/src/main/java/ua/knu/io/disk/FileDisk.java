package ua.knu.io.disk;

import lombok.Getter;

import java.io.File;
import java.io.RandomAccessFile;

// FileDisk use file to save FS
@Getter
public class FileDisk implements Disk {

    // size of block in bytes
    private final int blockSize;

    // Number of blocks in disks 
    private final int blockNumber;

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
    public byte[] readBlock(int blockID) {
        try {
            byte[] block= new byte[blockSize];

            diskFile.seek((long) blockID * blockSize);
            diskFile.read(block);

            return block;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void writeBlock(byte[] block, int blockID) {
        try {
            diskFile.seek((long) blockID * blockSize);
            diskFile.write(block);

        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public void init() {
        try {
            diskFile.seek(0);
            byte[] out = new byte[blockSize];
            
            for (int blockID = 0; blockID < blockNumber; blockID++) {
                writeBlock(out, blockID);
            }

        } catch (Exception err) {
            err.printStackTrace();
        }
    }
}
