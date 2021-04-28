package ua.knu.io.disk;

import ua.knu.elements.*;

public class DiskInitializer {

    public static boolean isInitialized;

    public static Disk initialize() {
        FileDisk disk = new FileDisk("src/main/resources/disk.bin", 64, 64);
        disk.init();

        byte[] row = disk.readBlock(0);
        Bitmap bm = new Bitmap();

        for (int blockID = 0; blockID < 7; blockID++) {
            bm.set(blockID);
        }
        
        bm.serialize(row, 0);
        disk.writeBlock(row, 0);
        
        for (int blockID = 1; blockID < 7; blockID++) {
            row = disk.readBlock(blockID);

            int currentPos = 0;
            
            Descriptor desc = new Descriptor();
            while (currentPos + desc.size() <= disk.getBlockSize()) {

                if (blockID != 1 || currentPos != 0) {
                    desc.setLength(-1);
                }

                desc.serialize(row, currentPos);
                currentPos += desc.size();
            }

            disk.writeBlock(row, blockID);
        }

        isInitialized = true;
        return disk;
    }
}
