package ua.knu.filesystem;

import ua.knu.filesystem.oft.*;
import ua.knu.io.disk.Disk;
import ua.knu.elements.*;
import ua.knu.errors.DirectoryEntryNotFoundException;
import ua.knu.errors.DirectoryFullException;

public class FileManager {
    OFTInterface oft;

    Disk disk;

    public FileManager(Disk disk) {
        oft = new OpenFileTable(disk);
        this.disk = disk;
    }

    public int getEmptyDirectoryEntryID() throws DirectoryFullException {
        DirectoryEntry entry = new DirectoryEntry();

        int absolutePos = 0;
        for (int blockNumber = 0; blockNumber < oft.getMaxDescriptorBlockNumber(0); blockNumber++) {
            byte[] data = oft.loadBlock(0, blockNumber);

            if (data == null) {
                byte[] zeroBlock = oft.getDisk().readBlock(0);
                Bitmap map = new Bitmap();
                map.deserialize(zeroBlock, 0);

                int freePos = map.nextFree();
                map.set(freePos);
                map.serialize(zeroBlock, 0);
                oft.getDisk().writeBlock(zeroBlock, 0);

                Descriptor dirDesc = oft.getDescriptorByID(0);
                int[] ptrs = dirDesc.getBlocks();
                ptrs[blockNumber] = freePos;
                dirDesc.setBlocks(ptrs);
                oft.setDescriptorByID(0, dirDesc);

                data = oft.getDisk().readBlock(freePos);
            }

            int currentPosition = 0;
            while (currentPosition + entry.size() <= oft.getDisk().blockSize()) {
                entry.deserialize(data, currentPosition);

                // Found one
                if (entry.getName() == 0) {
                    return absolutePos;
                }

                absolutePos++;
                currentPosition += entry.size();
            }
        }

        throw new DirectoryFullException("Directory is full");
    }

    public int getEmptyDescriptorID() throws DirectoryFullException {
        Descriptor desc = new Descriptor();

        int absolutePos = 0;
        // Iterate over blocks to find empty file descriptor
        for (int fdb = 1; fdb < 7; fdb++) {
            byte[] descriptorsBlock = oft.getDisk().readBlock(fdb);

            int currentPosition = 0;
            while (currentPosition + desc.size() <= oft.getDisk().blockSize()) {
                desc.deserialize(descriptorsBlock, currentPosition);

                // Found one
                if (desc.getLength() < 0) {
                    return absolutePos;
                }
                currentPosition += desc.size();
                absolutePos++;
            }
        }

        throw new DirectoryFullException("Directory is full");
    }

    public void setDirectoryEntryByID(DirectoryEntry entry, int id) throws DirectoryEntryNotFoundException {
        DirectoryEntry temp = new DirectoryEntry();

        int absolutePos = 0;
        for (int blockNumber = 0; blockNumber < oft.getMaxDescriptorBlockNumber(0); blockNumber++) {
            byte[] data = oft.loadBlock(0, blockNumber);

            if (data == null) {
                throw new DirectoryEntryNotFoundException("Cannot find directory entry with id = " + id);
            }

            int currentPosition = 0;
            while (currentPosition + temp.size() <= oft.getDisk().blockSize()) {
                temp.deserialize(data, currentPosition);

                if (absolutePos == id) {
                    entry.serialize(data, currentPosition);
                    oft.storeBlock(0);
                    return;
                }

                absolutePos++;
                currentPosition += temp.size();
            }
        }

        throw new DirectoryEntryNotFoundException("Cannot find directory entry with id = " + id);
    }

    public void create(int filename) throws DirectoryFullException, DirectoryEntryNotFoundException {
        DirectoryEntry entry = new DirectoryEntry();
        Descriptor descriptor = new Descriptor();

        int entryID = getEmptyDirectoryEntryID();
        int descriptorID = getEmptyDescriptorID();

        entry.setName(filename);
        entry.setDescriptorID(descriptorID);
        descriptor.setLength(0);

        int block = descriptorID / (disk.blockSize() / descriptor.size()) + 1;
        int offset = descriptorID % (disk.blockSize() / descriptor.size());
        byte[] data = disk.readBlock(block);
        descriptor.serialize(data, offset * descriptor.size());
        disk.writeBlock(data, block);

        setDirectoryEntryByID(entry, entryID);

        descriptor = oft.getDescriptorByID(0);
        descriptor.setLength(descriptor.getLength() + 1);
        oft.setDescriptorByID(0, descriptor);
    }

    public void remove(int filename) {
        DirectoryEntry entry = new DirectoryEntry();
        Descriptor desc = new Descriptor();

        // Iterate over all block pointers in direcotry descriptor
        for (int blockNumber = 0; blockNumber < oft.getMaxDescriptorBlockNumber(0); blockNumber++) {

            // Load next block
            byte[] data = oft.loadBlock(0, blockNumber);
            if (data == null) {
                return;
            }

            // Iterate over block to find entry with filename
            int currentPosition = 0;
            while (currentPosition + entry.size() <= oft.getDisk().blockSize()) {
                entry.deserialize(data, currentPosition);

                // Found one
                if (entry.getName() == filename) {
                    int descriptionPos = entry.getDescriptorID();

                    int block = descriptionPos / (oft.getDisk().blockSize() / desc.size()) + 1;
                    int offset = descriptionPos % (oft.getDisk().blockSize() / desc.size());
                    
                    byte[] fdBlock = oft.getDisk().readBlock(block);
                    desc.setLength(-1);
                    int[] descPtrs = desc.getBlocks();
                    for (int k = 0; k < descPtrs.length; k++) {
                        descPtrs[k] = 0;
                    }
                    desc.setBlocks(descPtrs);

                    desc.serialize(fdBlock, offset * desc.size());
                    oft.getDisk().writeBlock(fdBlock, block);

                    entry.setDescriptorID(0);
                    entry.setName(0);
                    entry.serialize(data, currentPosition);
                    oft.getDisk().writeBlock(data, oft.getDescriptorByID(0).getBlocks()[blockNumber]);

                    desc = oft.getDescriptorByID(0);
                    desc.setLength(desc.getLength() - 1);
                    oft.setDescriptorByID(0, desc);
                }

                currentPosition += entry.size();
            }
        }
    }
}
