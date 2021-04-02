package ua.knu.io.disk;

import ua.knu.elements.*;

public class DiskInitializer {
    public static Disk Initialize() {
        FileDisk disk = new FileDisk("disk.bin", 64, 64);
        disk.Init();

        byte[] row = disk.ReadBlock(0);
        Bitmap bm = new Bitmap();

        for (int i = 0; i < 7; i++) {
            bm.Set(i);
        }
        
        bm.Marshal(row, 0);
        disk.WriteBlock(row, 0);
        
        for (int blockID = 1; blockID < 7; blockID++) {
            row = disk.ReadBlock(blockID);

            int currentPos = 0;
            
            Descriptor desc = new Descriptor();
            while (currentPos + desc.Size() <= disk.BlockSize()) {

                if (blockID != 1 || currentPos != 0) {
                    desc.SetLength(-1);
                }

                desc.Marshal(row, currentPos);
                currentPos += desc.Size();
            }

            disk.WriteBlock(row, blockID);
        }


        return disk;
    }
}
