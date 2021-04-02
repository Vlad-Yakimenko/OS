package ua.knu.filesystem;

import ua.knu.filesystem.oft.*;
import ua.knu.io.disk.Disk;
import ua.knu.elements.*;

public class FileManager {
    OFTInterface oft;

    public FileManager(Disk disk) {
        oft = new OpenFileTable(disk);
    }

    public void Create(int filename) {
        DirectoryEntry entry = new DirectoryEntry();
        Descriptor desc = new Descriptor();

        // Iterate over all block pointers in direcotry descriptor
        for (int i = 0; i < oft.GetMaxDescriptorBlockNumber(0); i++) {

            // Load next block
            byte[] data = oft.LoadBlock(0, i);
            if (data == null) {
                byte[] zeroBlock = oft.GetDisk().ReadBlock(0);
                Bitmap map = new Bitmap();
                map.Unmarshal(zeroBlock, 0);

                int freePos = map.nextFree();
                map.Set(freePos);
                map.Marshal(zeroBlock, 0);
                oft.GetDisk().WriteBlock(zeroBlock, 0);

                Descriptor dirDesc = oft.GetDescriptorByID(0);
                int[] ptrs = dirDesc.GetBlocks();
                ptrs[i] = freePos;
                dirDesc.SetBlocks(ptrs);
                oft.SetDescriptorByID(0, dirDesc);

                data = oft.GetDisk().ReadBlock(freePos);
            }

            // Iterate over block to find directory entry with empty name
            int currentPosition = 0;
            while (currentPosition + entry.Size() <= oft.GetDisk().BlockSize()) {
                entry.Unmarshal(data, currentPosition);

                // Found one
                if (entry.GetName() == 0) {
                    Descriptor empty = new Descriptor();
                    int newDecriptorID = 0;

                    // Iterate over blocks to find empty file descriptor
                    for (int fdb = 1; fdb < 7; fdb++) {
                        byte[] descriptorsBlock = oft.GetDisk().ReadBlock(fdb);

                        int pos = 0;
                        while (pos + empty.Size() <= oft.GetDisk().BlockSize()) {
                            empty.Unmarshal(descriptorsBlock, pos);

                            // Found one
                            if (empty.GetLength() < 0) {
                                empty.SetLength(0);
                                empty.Marshal(descriptorsBlock, pos);
                                oft.GetDisk().WriteBlock(descriptorsBlock, fdb);


                                entry.SetName(filename);
                                entry.SetDescriptorID(newDecriptorID);
                                entry.Marshal(data, currentPosition);

                                Descriptor dirDescriptor = oft.GetDescriptorByID(0);
                                oft.GetDisk().WriteBlock(data, dirDescriptor.GetBlocks()[i]);
                                dirDescriptor.SetLength(dirDescriptor.GetLength() + 1);
                                oft.SetDescriptorByID(0, dirDescriptor);

                                return;
                            }
                            pos += empty.Size();
                            newDecriptorID++;
                        }
                    }
                }

                currentPosition += entry.Size();
            }
        }
    }

    public void Remove(int filename) {
        DirectoryEntry entry = new DirectoryEntry();
        Descriptor desc = new Descriptor();

        // Iterate over all block pointers in direcotry descriptor
        for (int i = 0; i < oft.GetMaxDescriptorBlockNumber(0); i++) {

            // Load next block
            byte[] data = oft.LoadBlock(0, i);
            if (data == null) {
                return;
            }

            // Iterate over block to find entry with filename
            int currentPosition = 0;
            while (currentPosition + entry.Size() <= oft.GetDisk().BlockSize()) {
                entry.Unmarshal(data, currentPosition);

                // Found one
                if (entry.GetName() == filename) {
                    int descriptionPos = entry.GetDescriptorID();

                    int block = descriptionPos / (oft.GetDisk().BlockSize() / desc.Size()) + 1;
                    int offset = descriptionPos % (oft.GetDisk().BlockSize() / desc.Size());
                    
                    byte[] fdBlock = oft.GetDisk().ReadBlock(block);
                    desc.SetLength(-1);
                    int[] descPtrs = desc.GetBlocks();
                    for (int k = 0; k < descPtrs.length; k++) {
                        descPtrs[k] = 0;
                    }
                    desc.SetBlocks(descPtrs);

                    desc.Marshal(fdBlock, offset * desc.Size());
                    oft.GetDisk().WriteBlock(fdBlock, block);

                    entry.SetDescriptorID(0);
                    entry.SetName(0);
                    entry.Marshal(data, currentPosition);
                    oft.GetDisk().WriteBlock(data, oft.GetDescriptorByID(0).GetBlocks()[i]);

                    desc = oft.GetDescriptorByID(0);
                    desc.SetLength(desc.GetLength() - 1);
                    oft.SetDescriptorByID(0, desc);
                }

                currentPosition += entry.Size();
            }
        }
    }
}
