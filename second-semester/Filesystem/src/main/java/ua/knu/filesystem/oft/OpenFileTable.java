package ua.knu.filesystem.oft;

import ua.knu.elements.Descriptor;
import ua.knu.elements.DirectoryEntry;
import ua.knu.io.disk.Disk;

import java.util.List;
import java.util.ArrayList;

class OFTEntry {
    // This value can be null!
    private byte[] block;
    private int currentPosition;
    private int descriptorPosition;

    public int getCurrentPosition() {
        return currentPosition;
    }
    
    public int getDescriptorPosition() {
        return descriptorPosition;
    }
    
    public byte[] getBlock() {
        return block;
    }

    public void setCurrentPosition(int pos) {
        currentPosition = pos;
    }
    
    public void setDescriptorPosition(int pos) {
        descriptorPosition = pos;
    }
    
    public void setBlock(byte[] b) {
        block = b;
    }
}

// OpenFileTable (OFT) represents a tableof open files
// @note Directory must always be open
// @note You should modify this class, and/or its interface
public class OpenFileTable implements OFTInterface {
    // entries stores OFT entries
    List<OFTEntry> entries;

    // empty stores true if OFT entry i is taken
    List<Boolean> empty;
    
    Disk disk;
    
    int MaxNumEntries = 4;

    public OpenFileTable(Disk d) {
        entries = new ArrayList<>(MaxNumEntries);
        empty = new ArrayList<>(MaxNumEntries);

        for (int i = 0; i < MaxNumEntries; i++) {
            entries.add(new OFTEntry());
            empty.add(new Boolean(true));
        }

        disk = d;

        // Load directory
        empty.set(0, new Boolean(true));
        entries.get(0).setDescriptorPosition(0);

        // get directory descriptor
        Descriptor directoryDescriptor = getDescriptorByID(0);
        // Load first block of directory
        if (directoryDescriptor.getBlocks()[0] != 0) {
            entries.get(0).setBlock(disk.readBlock(directoryDescriptor.getBlocks()[0]));
        }
    }

    @Override
    public int open(int filename) {
        DirectoryEntry entry = new DirectoryEntry();
        Descriptor desc = new Descriptor();

        // Iterate over all block pointers in direcotry descriptor
        for (int i = 0; i < getMaxDescriptorBlockNumber(0); i++) {

            // Load next block
            byte[] data = loadBlock(0, i);
            if (data == null) {
                return -1;
            }

            // Iterate over block to find directory entry with same name
            int currentPosition = 0;
            while (currentPosition + entry.size() <= disk.Blocksize()) {
                entry.deserialize(data, currentPosition);
                currentPosition += entry.size();

                // Found one
                if (entry.getName() == filename) {
                    // Reset directory OFT entry, beacause we iterated over it!
                    loadBlock(0, 0);
                    
                    int block = entry.getDescriptorID() / (disk.Blocksize() / desc.size()) + 1;
                    int offset = entry.getDescriptorID() % (disk.Blocksize() / desc.size());

                    data = disk.readBlock(block);
                    desc.deserialize(data, offset * desc.size());

                    // Find free OTF entry
                    for (int entryID = 0; entryID < MaxNumEntries; entryID++) {
                        if (empty.get(entryID)) {
                            // Load file data
                            empty.set(entryID, new Boolean(false));
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
        empty.set(id, new Boolean(true));

        return 0;
    }

    @Override
    public Descriptor getDescriptorByID(int id) {
        Descriptor desc = new Descriptor();

        int descriptorID = entries.get(id).getDescriptorPosition();

        int block = descriptorID / (disk.Blocksize() / desc.size()) + 1;
        int offset = descriptorID % (disk.Blocksize() / desc.size());

        byte[] data = disk.readBlock(block);
        desc.deserialize(data, offset * desc.size());

        return desc;
    }

    @Override
    public void setDescriptorByID(int id, Descriptor desc) {
        int descriptorID = entries.get(id).getDescriptorPosition();

        int block = descriptorID / (disk.Blocksize() / desc.size()) + 1;
        int offset = descriptorID % (disk.Blocksize() / desc.size());

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
            return null;
        }

        byte[] data = disk.readBlock(desc.getBlocks()[block]);

        entries.get(id).setBlock(data);
        entries.get(id).setCurrentPosition(disk.Blocksize() * block);

        return data;
    }

    @Override
    public void storeBlock(int id) {
        Descriptor desc = getDescriptorByID(id);
        int blockInDescriptor = entries.get(id).getCurrentPosition() / disk.Blocksize();
        int blockID = desc.getBlocks()[blockInDescriptor];

        byte[] data = entries.get(id).getBlock();
        disk.writeBlock(data, blockID);
    }

    public Disk getDisk() {
        return disk;
    }
}
