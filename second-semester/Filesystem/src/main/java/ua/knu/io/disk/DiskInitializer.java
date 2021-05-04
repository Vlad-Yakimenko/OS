package ua.knu.io.disk;

import lombok.Getter;
import ua.knu.elements.Bitmap;
import ua.knu.elements.Descriptor;

import static ua.knu.util.Constants.DISK_PATH;

public class DiskInitializer {

    private DiskInitializer() {
        throw new IllegalStateException("Utility class");
    }

    @Getter
    private static boolean isInitialized;

    public static Disk initialize() {
        FileDisk disk = new FileDisk(DISK_PATH, 64, 64);
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
