package ua.knu.filesystem.oft;

import ua.knu.elements.Descriptor;
import ua.knu.elements.DirectoryEntry;
import ua.knu.io.disk.Disk;

import java.util.List;
import java.util.ArrayList;

// OpenFileTable (OFT) represents a table of the open files
// @note Directory must always be open
// @note You should modify this class, and/or its interface
public class OpenFileTable implements IOpenFileTable {
    // entries stores OFT entries
    List<OFTEntry> entries;

    // empty stores true if OFT entry is taken
    List<Boolean> empty;
    
    Disk disk;
    
    int maxNumEntries = 4;

    public OpenFileTable(Disk d) {
        entries = new ArrayList<>(maxNumEntries);
        empty = new ArrayList<>(maxNumEntries);

        for (int entryID = 0; entryID < maxNumEntries; entryID++) {
            entries.add(new OFTEntry());
            empty.add(true);
        }

        disk = d;

        // Load directory
        empty.set(0, false);
        entries.get(0).setDescriptorPosition(0);

        // Get directory descriptor
        Descriptor directoryDescriptor = getDescriptorByID(0);
        // Load first block of directory
        if (directoryDescriptor.getBlocks()[0] != 0) {
            entries.get(0).setBlock(disk.readBlock(directoryDescriptor.getBlocks()[0]));
        }
    }

    @Override
    public int getMaxNumEntries() {
        return maxNumEntries;
    }

    @Override
    public int open(int filename) {
        DirectoryEntry entry = new DirectoryEntry();
        Descriptor desc = new Descriptor();

        // Iterate over all block pointers in directory descriptor
        for (int blockNumber = 0; blockNumber < getMaxDescriptorBlockNumber(0); blockNumber++) {

            // Load next block
            byte[] data = loadBlock(0, blockNumber);
            if (data == null) {
                return -1;
            }

            // Iterate over block to find directory entry with same name
            int currentPosition = 0;
            while (currentPosition + entry.size() <= disk.blockSize()) {
                entry.deserialize(data, currentPosition);
                currentPosition += entry.size();

                // Found one
                if (entry.getName() == filename) {
                    // Reset directory OFT entry, because we iterated over it!
                    loadBlock(0, 0);
                    
                    int block = entry.getDescriptorID() / (disk.blockSize() / desc.size()) + 1;
                    int offset = entry.getDescriptorID() % (disk.blockSize() / desc.size());

                    data = disk.readBlock(block);
                    desc.deserialize(data, offset * desc.size());

                    // Find free OTF entry
                    for (int entryID = 0; entryID < maxNumEntries; entryID++) {
                        if (empty.get(entryID)) {
                            // Load file data
                            empty.set(entryID, false);
                            entries.get(entryID).setCurrentPosition(0);
                            entries.get(entryID).setDescriptorPosition(entry.getDescriptorID());

                            if (desc.getBlocks()[0] != 0) {
                                entries.get(entryID).setBlock(disk.readBlock(desc.getBlocks()[0]));
                            } else {
                                entries.get(entryID).setBlock(null);
                            }

                            return entryID;
                        }
                    }

                    return -1;
                }
            }
        }

        return -1;
    }

    @Override
    public int close(int id) {
        storeBlock(id);
        empty.set(id, true);

        return 0;
    }

    @Override
    public OFTEntry getEntryById(int id) {
        return entries.get(id);
    }

    @Override
    public Descriptor getDescriptorByID(int id) {
        Descriptor desc = new Descriptor();

        int descriptorID = entries.get(id).getDescriptorPosition();

        int block = descriptorID / (disk.blockSize() / desc.size()) + 1;
        int offset = descriptorID % (disk.blockSize() / desc.size());

        byte[] data = disk.readBlock(block);
        desc.deserialize(data, offset * desc.size());

        return desc;
    }

    @Override
    public void setDescriptorByID(int id, Descriptor desc) {
        int descriptorID = entries.get(id).getDescriptorPosition();

        int block = descriptorID / (disk.blockSize() / desc.size()) + 1;
        int offset = descriptorID % (disk.blockSize() / desc.size());

        byte[] data = disk.readBlock(block);
        desc.serialize(data, offset * desc.size());
        disk.writeBlock(data, block);
    }

    @Override
    public int getMaxDescriptorBlockNumber(int id) {
        return 3;
    }

    @Override
    public byte[] loadBlock(int id, int block) {
        Descriptor desc = getDescriptorByID(id);
        if (desc.getBlocks()[block] <= 0) {
            entries.get(id).setBlock(null);
            entries.get(id).setCurrentPosition(disk.blockSize() * block);
            return null;
        }

        byte[] data = disk.readBlock(desc.getBlocks()[block]);

        entries.get(id).setBlock(data);
        entries.get(id).setCurrentPosition(disk.blockSize() * block);

        return data;
    }

    @Override
    public void storeBlock(int id) {
        Descriptor desc = getDescriptorByID(id);
        int blockInDescriptor = entries.get(id).getCurrentPosition() / disk.blockSize();
        int blockID = desc.getBlocks()[blockInDescriptor];

        byte[] data = entries.get(id).getBlock();
        disk.writeBlock(data, blockID);
    }

    public Disk getDisk() {
        return disk;
    }
}
