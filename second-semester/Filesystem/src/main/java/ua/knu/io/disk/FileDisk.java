package ua.knu.io.disk;

import java.io.File;
import java.io.RandomAccessFile;

// FileDisk use file to save FS 
public class FileDisk implements Disk {
    // size of block in bytes
    private int blocksize;

    // Number of blocks in disks 
    private int blockNumber;

    private RandomAccessFile diskFile;

    public FileDisk(String filename, int blocksize, int blockNumber) {
        this.blockNumber = blockNumber;
        this.blocksize = blocksize;

        try {
            diskFile = new RandomAccessFile(new File(filename), "rw");
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    @Override
    public byte[] readBlock(int blockID) {
        try {
            byte[] block= new byte[blocksize];

            diskFile.seek(blockID * blocksize);
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
            diskFile.seek(blockID * blocksize);
            diskFile.write(block);

        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public void Init() {
        try {
            diskFile.seek(0);
            byte[] out = new byte[blocksize];
            
            for (int blockID = 0; blockID < blockNumber; blockID++) {
                writeBlock(out, blockID);
            }

        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    @Override
    public int Blocksize() {
        return blocksize;
    }

    @Override
    public int BlockNumber() {
        return blockNumber;
    }
}