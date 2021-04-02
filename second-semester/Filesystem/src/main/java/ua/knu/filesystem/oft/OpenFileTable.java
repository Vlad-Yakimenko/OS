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

    public int GetCurrentPosition() {
        return currentPosition;
    }
    
    public int GetDescriptorPosition() {
        return descriptorPosition;
    }
    
    public byte[] GetBlock() {
        return block;
    }

    public void SetCurrentPosition(int pos) {
        currentPosition = pos;
    }
    
    public void SetDescriptorPosition(int pos) {
        descriptorPosition = pos;
    }
    
    public void SetBlock(byte[] b) {
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
        entries.get(0).SetDescriptorPosition(0);

        // Get directory descriptor
        Descriptor directoryDescriptor = GetDescriptorByID(0);
        // Load first block of directory
        if (directoryDescriptor.GetBlocks()[0] != 0) {
            entries.get(0).SetBlock(disk.ReadBlock(directoryDescriptor.GetBlocks()[0]));
        }
    }

    @Override
    public int Open(int filename) {
        DirectoryEntry entry = new DirectoryEntry();
        Descriptor desc = new Descriptor();

        // Iterate over all block pointers in direcotry descriptor
        for (int i = 0; i < GetMaxDescriptorBlockNumber(0); i++) {

            // Load next block
            byte[] data = LoadBlock(0, i);
            if (data == null) {
                return -1;
            }

            // Iterate over block to find directory entry with same name
            int currentPosition = 0;
            while (currentPosition + entry.Size() <= disk.BlockSize()) {
                entry.Unmarshal(data, currentPosition);
                currentPosition += entry.Size();

                // Found one
                if (entry.GetName() == filename) {
                    // Reset directory OFT entry, beacause we iterated over it!
                    LoadBlock(0, 0);
                    
                    int block = entry.GetDescriptorID() / (disk.BlockSize() / desc.Size()) + 1;
                    int offset = entry.GetDescriptorID() % (disk.BlockSize() / desc.Size());

                    data = disk.ReadBlock(block);
                    desc.Unmarshal(data, offset * desc.Size());

                    // Find free OTF entry
                    for (int entryID = 0; entryID < MaxNumEntries; entryID++) {
                        if (empty.get(entryID)) {
                            // Load file data
                            empty.set(entryID, new Boolean(false));
                            entries.get(entryID).SetCurrentPosition(0);
                            entries.get(entryID).SetDescriptorPosition(entry.GetDescriptorID());

                            if (desc.GetBlocks()[0] != 0) {
                                entries.get(entryID).SetBlock(disk.ReadBlock(desc.GetBlocks()[0]));
                            } else {
                                entries.get(entryID).SetBlock(null);
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
    public int Close(int id) {
        StoreBlock(id);
        empty.set(id, new Boolean(true));

        return 0;
    }

    @Override
    public Descriptor GetDescriptorByID(int id) {
        Descriptor desc = new Descriptor();

        int descriptorID = entries.get(id).GetDescriptorPosition();

        int block = descriptorID / (disk.BlockSize() / desc.Size()) + 1;
        int offset = descriptorID % (disk.BlockSize() / desc.Size());

        byte[] data = disk.ReadBlock(block);
        desc.Unmarshal(data, offset * desc.Size());

        return desc;
    }

    @Override
    public void SetDescriptorByID(int id, Descriptor desc) {
        int descriptorID = entries.get(id).GetDescriptorPosition();

        int block = descriptorID / (disk.BlockSize() / desc.Size()) + 1;
        int offset = descriptorID % (disk.BlockSize() / desc.Size());

        byte[] data = disk.ReadBlock(block);
        desc.Marshal(data, offset * desc.Size());
        disk.WriteBlock(data, block);
    }

    @Override
    public int GetMaxDescriptorBlockNumber(int id) {
        return 3;
    }

    @Override
    public byte[] LoadBlock(int id, int block) {
        Descriptor desc = GetDescriptorByID(id);
        if (desc.GetBlocks()[block] <= 0) {
            return null;
        }

        byte[] data = disk.ReadBlock(desc.GetBlocks()[block]);

        entries.get(id).SetBlock(data);
        entries.get(id).SetCurrentPosition(disk.BlockSize() * block);

        return data;
    }

    @Override
    public void StoreBlock(int id) {
        Descriptor desc = GetDescriptorByID(id);
        int blockInDescriptor = entries.get(id).GetCurrentPosition() / disk.BlockSize();
        int blockID = desc.GetBlocks()[blockInDescriptor];

        byte[] data = entries.get(id).GetBlock();
        disk.WriteBlock(data, blockID);
    }

    public Disk GetDisk() {
        return disk;
    }
}
