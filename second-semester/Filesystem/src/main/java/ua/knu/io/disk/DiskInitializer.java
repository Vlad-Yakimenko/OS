package ua.knu.io.disk;

import ua.knu.elements.*;

public class DiskInitializer {
    public static Disk Initialize() {
        FileDisk disk = new FileDisk("disk.bin", 64, 64);
        disk.Init();

        byte[] row = disk.ReadBlock(0);
        Bitmap bm = new Bitmap();
        bm.Marshal(row, 0);
        disk.WriteBlock(row, 0);
        
        for (int blockID = 1; blockID < 7; blockID++) {
            row = disk.ReadBlock(blockID);

            int currentPos = 0;
            
            while (currentPos < disk.BlockSize()) {
                Descriptor desc = new Descriptor();
                if (currentPos + desc.Size() >= disk.BlockSize()) {
                    break;
                }

                desc.Marshal(row, currentPos);
                currentPos += desc.Size();
            }

            disk.WriteBlock(row, blockID);
        }


        return disk;
    }
}
