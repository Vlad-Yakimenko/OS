package ua.knu.filesystem;

import lombok.experimental.FieldDefaults;
import ua.knu.filesystem.oft.*;
import ua.knu.io.disk.Disk;
import ua.knu.elements.*;
import ua.knu.exceptions.DirectoryEntryNotFoundException;
import ua.knu.exceptions.DirectoryFullException;

@FieldDefaults(makeFinal = true)
public class FileManager {

    private OpenFileTable oft;
    private Disk disk;

    public FileManager(Disk disk) {
        this.oft = new OpenFileTable(disk);
        this.disk = disk;
    }

    public int open(int filename) {
        return oft.open(filename);
    }

    public int close(int filename) {
        return oft.close(filename);
    }

    public void create(int filename) throws DirectoryException {
        DirectoryEntry entry = new DirectoryEntry();
        Descriptor descriptor = new Descriptor();

        int entryID = getEmptyDirectoryEntryID();
        int descriptorID = getEmptyDescriptorID();

        entry.setName(filename);
        entry.setDescriptorID(descriptorID);
        descriptor.setLength(0);

        int block = descriptorID / (disk.getBlockSize() / descriptor.size()) + 1;
        int offset = descriptorID % (disk.getBlockSize() / descriptor.size());
        byte[] data = disk.readBlock(block);
        descriptor.serialize(data, offset * descriptor.size());
        disk.writeBlock(data, block);

        setDirectoryEntryByID(entry, entryID);

        descriptor = oft.getDescriptorByID(0);
        descriptor.setLength(descriptor.getLength() + 1);
        oft.setDescriptorByID(0, descriptor);
    }

    public byte[] read(int id, int count) throws ReadFileException {
        int blockSize = oft.getDisk().blockSize();

        if (id < 0 || id > oft.getMaxNumEntries() - 1) {
            throw new ReadFileException("Id is out of range");
        }

        OFTEntry oftEntry = oft.getEntryById(id);

        if (count <= 0 || count > blockSize * oft.getMaxDescriptorBlockNumber(0)) {
            throw new ReadFileException("Count is out of range");
        }

        byte[] readBytes = {};

        while (true) {
            int currentPosition = oftEntry.getCurrentPosition();
            int currentBlockNumber = (currentPosition / blockSize) + 1; // 1 2 3

            // 0-63 64-127 128-191
            if ((currentBlockNumber * blockSize - 1) - currentPosition >= count) { // data in scope of currently loaded buffer
                int newPosition = currentPosition + count;
                byte[] block = oftEntry.getBlock();

                if (block == null) {
                    return readBytes;
                }

                readBytes = ArrayUtils.addAll(
                    readBytes, Arrays.copyOfRange(oftEntry.getBlock(),
                    currentPosition % blockSize, newPosition % blockSize)
                );

                oftEntry.setCurrentPosition(newPosition);
                break;
            }

            readBytes = ArrayUtils.addAll(
                readBytes, Arrays.copyOfRange(oftEntry.getBlock(),
                currentPosition % blockSize, oftEntry.getBlock().length)
            );

            int amountOfReadBytes = oftEntry.getBlock().length - (currentPosition % blockSize);
            count -= amountOfReadBytes;

            oft.storeBlock(id);
            oft.loadBlock(id, currentBlockNumber);
        }

        return readBytes;
    }

    public void seek(int id, int pos) throws SeekFileException {
        int blockSize = oft.getDisk().blockSize();

        if (id < 0 || id > oft.getMaxNumEntries() - 1) {
            throw new SeekFileException("Id is out of range");
        }

        int endPos = oft.getDescriptorByID(id).getLength();

        if (pos < 0 || pos > endPos) {
            throw new SeekFileException("Position is out of range");
        }

        OFTEntry entry = oft.getEntryById(id);
        if (endPos > 0) {
            oft.storeBlock(id);
        }

        int blockNumber = (pos / blockSize);
        oft.loadBlock(id, blockNumber);
        entry.setCurrentPosition(pos);
    }

    public ArrayList<Pair<?, ?>> directory() {
        ArrayList<Pair<?, ?>> files = new ArrayList<>();
        DirectoryEntry entry = new DirectoryEntry();
        Descriptor desc = new Descriptor();

        for (int blockNumber = 0; blockNumber < oft.getMaxDescriptorBlockNumber(0); blockNumber++) {
            byte[] data = oft.loadBlock(0, blockNumber);
            if (data == null) {
                return files;
            }

            int currentPosition = 0;
            int fileCounter = 0;
            int filesAmount = oft.getDescriptorByID(0).getLength();

            while (fileCounter != filesAmount) {
                entry.deserialize(data, currentPosition);
                int descriptorID = entry.getDescriptorID();

                if (descriptorID == 0) { // deleted entry
                    currentPosition += entry.size();
                    fileCounter++;
                    continue;
                }

                int block = descriptorID / (disk.blockSize() / desc.size()) + 1;
                int offset = descriptorID % (disk.blockSize() / desc.size());

                byte[] blockData = disk.readBlock(block);
                desc.deserialize(blockData, offset * desc.size());

                files.add(Pair.of(entry.getName(), desc.getLength()));

                currentPosition += entry.size();
                fileCounter++;
            }
        }

        return files;
    }

    public void write(int id, String str) throws FileException {
        Descriptor file = oft.getDescriptorByID(id);
        OFTEntry entry = oft.getEntryById(id);

        for (int symbolID = 0; symbolID < str.length(); symbolID++) {
            if (entry.getCurrentPosition() != 0 && (entry.getCurrentPosition()) % oft.getDisk().blockSize() == 0) {
                if ((entry.getCurrentPosition()) / oft.getDisk().blockSize() > 2) {
                    oft.setDescriptorByID(id, file);
                    oft.storeBlock(id);
                    throw new FileException("File " + id + " is full");
                }

                entry.setCurrentPosition(entry.getCurrentPosition() - 1);
                oft.storeBlock(id);
                oft.setDescriptorByID(id, file);
                oft.loadBlock(id, (entry.getCurrentPosition() + 1) / oft.getDisk().blockSize());
                entry.setCurrentPosition(entry.getCurrentPosition() + 1);
            }

            if (entry.getBlock() == null) {
                int blockNumber = entry.getCurrentPosition() / oft.getDisk().blockSize();
                byte[] zeroBlock = oft.getDisk().readBlock(0);
                Bitmap map = new Bitmap();
                map.deserialize(zeroBlock, 0);

                int freePos = map.nextFree();
                map.set(freePos);
                map.serialize(zeroBlock, 0);
                oft.getDisk().writeBlock(zeroBlock, 0);

                int[] ptrs = file.getBlocks();
                ptrs[blockNumber] = freePos;
                file.setBlocks(ptrs);
                oft.setDescriptorByID(id, file);

                oft.loadBlock(id, blockNumber);
                entry = oft.getEntryById(id);
            }

            byte[] block = entry.getBlock();
            block[entry.getCurrentPosition() % oft.getDisk().blockSize()] = (byte) str.charAt(symbolID);
            file.setLength(file.getLength() + 1);
            entry.setCurrentPosition(entry.getCurrentPosition() + 1);
        }

        oft.setDescriptorByID(id, file);
        oft.storeBlock(id);
    }

    public void remove(int filename) {
        DirectoryEntry entry = new DirectoryEntry();
        Descriptor desc = new Descriptor();

        // Iterate over all block pointers in directory descriptor
        for (int blockNumber = 0; blockNumber < oft.getMaxDescriptorBlockNumber(0); blockNumber++) {
            // Load next block
            byte[] data = oft.loadBlock(0, blockNumber);
            if (data == null) {
                return;
            }

            // Iterate over block to find entry with filename
            int currentPosition = 0;
            while (currentPosition + entry.size() <= oft.getDisk().getBlockSize()) {
                entry.deserialize(data, currentPosition);

                // Found one
                if (entry.getName() == filename) {
                    byte[] zeroBlock = oft.getDisk().readBlock(0);
                    Bitmap map = new Bitmap();
                    map.deserialize(zeroBlock, 0);

                    int descriptionPos = entry.getDescriptorID();

                    int block = descriptionPos / (oft.getDisk().getBlockSize() / desc.size()) + 1;
                    int offset = descriptionPos % (oft.getDisk().getBlockSize() / desc.size());
                    byte[] fdBlock = oft.getDisk().readBlock(block);
                    desc.deserialize(fdBlock, offset * desc.size());
                    desc.setLength(-1);
                    int[] descPtrs = desc.getBlocks();
                    for (int k = 0; k < descPtrs.length; k++) {
                        if (descPtrs[k] > 0) {
                            map.reset(descPtrs[k]);
                        }
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

                    map.serialize(zeroBlock, 0);
                    oft.getDisk().writeBlock(zeroBlock, 0);
                }

                currentPosition += entry.size();
            }
        }
    }

    private void setDirectoryEntryByID(DirectoryEntry entry, int id) throws DirectoryException {
        DirectoryEntry temp = new DirectoryEntry();

        int absolutePos = 0;
        for (int blockNumber = 0; blockNumber < oft.getMaxDescriptorBlockNumber(0); blockNumber++) {
            byte[] data = oft.loadBlock(0, blockNumber);

            if (data == null) {
                throw new DirectoryException("Cannot find directory entry with id = " + id);
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

        throw new DirectoryException("Cannot find directory entry with id = " + id);
    }

    private int getEmptyDirectoryEntryID() throws DirectoryException {
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

        throw new DirectoryException("Directory is full");
    }

    private int getEmptyDescriptorID() throws DirectoryException {
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

        throw new DirectoryException("Directory is full");
    }
}
