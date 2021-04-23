package ua.knu;

import ua.knu.io.disk.DiskInitializer;
import ua.knu.elements.Bitmap;
import ua.knu.filesystem.FileManager;
import ua.knu.io.disk.Disk;

public class Main {
    public static void main(String[] args) throws Exception {
        Disk disk = DiskInitializer.initialize();

        FileManager fm = new FileManager(disk);

        byte[] block = disk.readBlock(0);
        Bitmap bm = new Bitmap();
        bm.deserialize(block, 0);
        System.out.println(bm.getMap());

        fm.create(1);

        block = disk.readBlock(0);
        bm.deserialize(block, 0);
        System.out.println(bm.getMap());


       fm.create(2);

       block = disk.readBlock(0);
       bm.deserialize(block, 0);
       System.out.println(bm.getMap());

       fm.create(3);

       block = disk.readBlock(0);
       bm.deserialize(block, 0);
       System.out.println(bm.getMap());


       fm.create(4);

       block = disk.readBlock(0);
       bm.deserialize(block, 0);
       System.out.println(bm.getMap());


       fm.create(5);

       block = disk.readBlock(0);
       bm.deserialize(block, 0);
       System.out.println(bm.getMap());


       fm.create(6);

       block = disk.readBlock(0);
       bm.deserialize(block, 0);
       System.out.println(bm.getMap());


       fm.create(7);

       block = disk.readBlock(0);
       bm.deserialize(block, 0);
       System.out.println(bm.getMap());


       fm.create(8);

       block = disk.readBlock(0);
       bm.deserialize(block, 0);
       System.out.println(bm.getMap());


       fm.create(9);
       fm.create(10);


       fm.remove(7);
       fm.remove(8);

       fm.create(11);
       fm.create(12);
    }
}
