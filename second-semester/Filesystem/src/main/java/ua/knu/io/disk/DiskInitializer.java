package ua.knu.io.disk;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import ua.knu.elements.Bitmap;
import ua.knu.elements.Descriptor;

import java.io.File;

import static ua.knu.util.Constants.DISK_PATH;

public class DiskInitializer {

    private DiskInitializer() {
        throw new IllegalStateException("Utility class");
    }

    @Getter
    @Setter
    private static boolean isInitialized;

    public static Disk initialize(String diskName) {
        FileDisk disk = new FileDisk(String.format(DISK_PATH, diskName), 64, 64);
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

    public static Disk open(String diskName) {
        isInitialized = true;

        String filename = String.format(DISK_PATH, diskName);
        val isFileExists = new File(filename).exists();

        if (isFileExists) {
            return new FileDisk(filename, 64, 64);
        }

        throw new IllegalArgumentException("this disk was not initialized");
    }
}
