package ua.knu.filesystem;

import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import ua.knu.elements.Bitmap;
import ua.knu.elements.Descriptor;
import ua.knu.elements.DirectoryEntry;
import ua.knu.exceptions.FileOperationException;
import ua.knu.filesystem.oft.*;
import ua.knu.io.disk.Disk;
import ua.knu.util.FilenameConverter;

import java.util.*;
import java.util.stream.Collectors;

public class FileManagerImpl implements FileManager {

    private static final String ID_IS_OUT_OF_RANGE = "Id = %s is out of range";
    private static final String COUNT_IS_OUT_OF_RANGE = "Count = %s is out of range";
    private static final String POSITION_IS_OUT_OF_RANGE = "Position = %s is out of range";
    private static final String CANT_FIND_DIRECTORY_ENTRY_WITH_ID = "Can't find directory entry with id = %s";
    private static final String DIRECTORY_IS_FULL = "Directory is full";
    private static final String FILE_WITH_ID_IS_NOT_OPEN = "File with id = %s is not open";
    private static final String FILE_WITH_ID_IS_FULL = "File with id = %s is full";
    private static final String FILE_DOES_NOT_EXIST = "File with filename = %s does not exist";
    private static final String FILE_DOES_EXIST = "File with filename = %s exists";
    private static final String DISK_IS_FULL = "Disk is full";

    private OpenFileTable oft;
    private Disk disk;
    Map<Integer, FileMetadata> files;

    public void init(Disk disk) {
        this.oft = new OpenFileTableImpl(disk);
        this.disk = disk;
        this.files = new HashMap<>();
        loadFiles();
    }

    @Override
    public int open(int filename) throws FileOperationException {
        return oft.open(filename);
    }

    @Override
    public int close(int id) throws FileOperationException {
        return oft.close(id);
    }

    @Override
    public void create(int filename) throws FileOperationException {
        if (files.containsKey(filename)) {
            throw new FileOperationException(String.format(FILE_DOES_EXIST, FilenameConverter.convertToString(filename)));
        }

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

        files.put(filename, new FileMetadata(entry.getName(), entry.getDescriptorID(), entryID, 0));
    }

    @Override
    public byte[] read(int id, int count) throws FileOperationException {
        loadFiles();

        int blockSize = oft.getDisk().getBlockSize();

        if (id < 0 || id > oft.getMaxNumEntries() - 1) {
            throw new FileOperationException(String.format(ID_IS_OUT_OF_RANGE, id));
        }

        if (oft.isEmptyEntry(id)) {
            throw new FileOperationException(String.format(FILE_WITH_ID_IS_NOT_OPEN, id));
        }

        OftEntry oftEntry = oft.getEntryById(id);

        if (count <= 0 || count > blockSize * oft.getMaxDescriptorBlockNumber(0)) {
            throw new FileOperationException(String.format(COUNT_IS_OUT_OF_RANGE, count));
        }

        byte[] readBytes = {};

        for (val file : files.entrySet()) {
            if (
                    file.getValue().descriptorID == oftEntry.getDescriptorPosition() &&
                            oftEntry.getCurrentPosition() + count > file.getValue().getSize()
            ) {
                count = file.getValue().getSize() - oftEntry.getCurrentPosition();
                break;
            }
        }

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

    @Override
    public void seek(int id, int pos) throws FileOperationException {
        int blockSize = oft.getDisk().getBlockSize();

        if (id < 0 || id > oft.getMaxNumEntries() - 1) {
            throw new FileOperationException(String.format(ID_IS_OUT_OF_RANGE, id));
        }

        if (oft.isEmptyEntry(id)) {
            throw new FileOperationException(String.format(FILE_WITH_ID_IS_NOT_OPEN, id));
        }

        int endPos = oft.getDescriptorByID(id).getLength();

        if (pos < 0 || pos > endPos || pos > disk.getBlockSize()*3) {
            throw new FileOperationException(String.format(POSITION_IS_OUT_OF_RANGE, pos));
        }

        OftEntry entry = oft.getEntryById(id);
        if (endPos > 0) {
            oft.storeBlock(id);
        }

        int blockNumber = (pos / blockSize);
        oft.loadBlock(id, blockNumber);
        entry.setCurrentPosition(pos);
    }

    @Override
    public List<Pair<Integer, Integer>> directory() {
        loadFiles();
        return files
                .entrySet()
                .stream()
                .map(file -> Pair.of(file.getKey(), file.getValue().getSize()))
                .collect(Collectors.toList());
    }

    @Override
    public void write(int id, String str) throws FileOperationException {
        if (id < 0 || id > oft.getMaxNumEntries() - 1) {
            throw new FileOperationException(String.format(ID_IS_OUT_OF_RANGE, id));
        }

        if (oft.isEmptyEntry(id)) {
            throw new FileOperationException(String.format(FILE_WITH_ID_IS_NOT_OPEN, id));
        }

        Descriptor file = oft.getDescriptorByID(id);
        OftEntry entry = oft.getEntryById(id);

        for (int symbolID = 0; symbolID < str.length(); symbolID++) {
            if ((entry.getCurrentPosition()) / oft.getDisk().getBlockSize() >= 3) {
                oft.setDescriptorByID(id, file);
                oft.storeBlock(id);
                throw new FileOperationException(String.format(FILE_WITH_ID_IS_FULL, id));
            }

            if (entry.getCurrentPosition() != 0 && (entry.getCurrentPosition()) % oft.getDisk().getBlockSize() == 0) {
                entry.setCurrentPosition(entry.getCurrentPosition() - 1);
                oft.storeBlock(id);
                oft.setDescriptorByID(id, file);
                oft.loadBlock(id, (entry.getCurrentPosition() + 1) / oft.getDisk().getBlockSize());
            }

            if (entry.getBlock() == null) {
                if (entry.getCurrentPosition() == oft.getDisk().getBlockSize() * 3) {
                    throw new FileOperationException(String.format(FILE_WITH_ID_IS_FULL, id));
                }
                int blockNumber = entry.getCurrentPosition() / oft.getDisk().getBlockSize();
                byte[] zeroBlock = oft.getDisk().readBlock(0);

                Bitmap map = new Bitmap();
                map.deserialize(zeroBlock, 0);

                int freePos = map.nextFree();
                if (freePos <= 0) {
                    throw new FileOperationException(DISK_IS_FULL);
                }
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
            block[entry.getCurrentPosition() % oft.getDisk().getBlockSize()] = (byte) str.charAt(symbolID);
            if (file.getLength() <= entry.getCurrentPosition()) {
                file.setLength(file.getLength() + 1);
            }
            entry.setCurrentPosition(entry.getCurrentPosition() + 1);
        }

        entry.setCurrentPosition(entry.getCurrentPosition() - 1);
        oft.storeBlock(id);
        oft.setDescriptorByID(id, file);
        entry.setCurrentPosition(entry.getCurrentPosition() + 1);
    }

    public void remove(int filename) throws FileOperationException {
        if (!files.containsKey(filename)) {
            throw new FileOperationException(String.format(FILE_DOES_NOT_EXIST, FilenameConverter.convertToString(filename)));
        }

        DirectoryEntry entry = new DirectoryEntry();
        Descriptor desc = new Descriptor();

        // Iterate over all block pointers in directory descriptor
        for (int blockNumber = 0; blockNumber < oft.getMaxDescriptorBlockNumber(0); blockNumber++) {
            // Load next block
            byte[] data = oft.loadBlock(0, blockNumber);
            if (data == null) {
                throw new FileOperationException(String.format(FILE_DOES_NOT_EXIST, FilenameConverter.convertToString(filename)));
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

                    files.remove(filename);

                    oft.delete(descriptionPos);

                    return;
                }

                currentPosition += entry.size();
            }
        }
    }

    private void setDirectoryEntryByID(DirectoryEntry entry, int id) throws FileOperationException {
        DirectoryEntry temp = new DirectoryEntry();

        int absolutePos = 0;
        for (int blockNumber = 0; blockNumber < oft.getMaxDescriptorBlockNumber(0); blockNumber++) {
            byte[] data = oft.loadBlock(0, blockNumber);

            if (data == null) {
                throw new FileOperationException(String.format(CANT_FIND_DIRECTORY_ENTRY_WITH_ID, id));
            }

            int currentPosition = 0;
            while (currentPosition + temp.size() <= oft.getDisk().getBlockSize()) {
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

        throw new FileOperationException(String.format(CANT_FIND_DIRECTORY_ENTRY_WITH_ID, id));
    }

    private int getEmptyDirectoryEntryID() throws FileOperationException {
        DirectoryEntry entry = new DirectoryEntry();

        int absolutePos = 0;
        for (int blockNumber = 0; blockNumber < oft.getMaxDescriptorBlockNumber(0); blockNumber++) {
            byte[] data = oft.loadBlock(0, blockNumber);

            if (data == null) {
                byte[] zeroBlock = oft.getDisk().readBlock(0);
                Bitmap map = new Bitmap();
                map.deserialize(zeroBlock, 0);

                int freePos = map.nextFree();
                if (freePos <= 0) {
                    throw new FileOperationException(DISK_IS_FULL);
                }
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
            while (currentPosition + entry.size() <= oft.getDisk().getBlockSize()) {
                entry.deserialize(data, currentPosition);

                // Found one
                if (entry.getName() == 0) {
                    return absolutePos;
                }

                absolutePos++;
                currentPosition += entry.size();
            }
        }

        throw new FileOperationException(DIRECTORY_IS_FULL);
    }

    private int getEmptyDescriptorID() throws FileOperationException {
        Descriptor desc = new Descriptor();

        int absolutePos = 0;
        // Iterate over blocks to find empty file descriptor
        for (int fdb = 1; fdb < 7; fdb++) {
            byte[] descriptorsBlock = oft.getDisk().readBlock(fdb);

            int currentPosition = 0;
            while (currentPosition + desc.size() <= oft.getDisk().getBlockSize()) {
                desc.deserialize(descriptorsBlock, currentPosition);

                // Found one
                if (desc.getLength() < 0) {
                    return absolutePos;
                }

                currentPosition += desc.size();
                absolutePos++;
            }
        }

        throw new FileOperationException(DIRECTORY_IS_FULL);
    }

    private void loadFiles() {
        DirectoryEntry entry = new DirectoryEntry();
        Descriptor desc = new Descriptor();

        int currentPosition = 0;
        int currentID = 0;

        for (int blockNumber = 0; blockNumber < oft.getMaxDescriptorBlockNumber(0); blockNumber++) {
            currentPosition = 0;

            while (currentPosition < disk.getBlockSize()) {
                byte[] data = oft.loadBlock(0, blockNumber);
                if (data == null) {
                    return;
                }
                entry.deserialize(data, currentPosition);
                int descriptorID = entry.getDescriptorID();

                if (descriptorID == 0) { // deleted entry
                    currentPosition += entry.size();
                    currentID++;
                    continue;
                }

                int block = descriptorID / (disk.getBlockSize() / desc.size()) + 1;
                int offset = descriptorID % (disk.getBlockSize() / desc.size());

                byte[] blockData = disk.readBlock(block);
                desc.deserialize(blockData, offset * desc.size());

                files.put(entry.getName(), new FileMetadata(entry.getName(), entry.getDescriptorID(), currentID, desc.getLength()));

                currentPosition += entry.size();
                currentID++;
            }
        }
    }
}
