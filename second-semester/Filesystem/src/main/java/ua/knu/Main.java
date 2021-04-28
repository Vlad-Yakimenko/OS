package ua.knu;

import org.apache.commons.lang3.tuple.Pair;
import ua.knu.io.disk.DiskInitializer;
import ua.knu.elements.Bitmap;
import ua.knu.filesystem.FileManager;
import ua.knu.io.disk.Disk;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws Exception {
        Disk disk = DiskInitializer.initialize();

        FileManager fm = new FileManager(disk);

//        byte[] block = disk.readBlock(0);
//        Bitmap bm = new Bitmap();
//        bm.deserialize(block, 0);
//        System.out.println(bm.getMap());

        fm.create(1);

//        block = disk.readBlock(0);
//        bm.deserialize(block, 0);
//        System.out.println(bm.getMap());


       fm.create(2);

       fm.create(3);

       fm.create(4);

       //int id = fm.open(2);

       //fm.seek(id, 0);


       //fm.directory();
//
//       fm.create(5);
//
//       fm.create(6);
//
//       fm.create(7);
//
//       fm.create(8);
//
//       fm.create(9);
//
//       fm.create(10);
//
//       fm.remove(7);
//       fm.remove(8);
//
//       fm.create(11);
//       fm.create(12);

        ArrayList<Pair<?, ?>> files = fm.directory();
        System.out.println(files);

        fm.create(3);
        fm.create(7);
        fm.create(9);
        int id = fm.open(3);
        fm.write(id, "Hello, world!");
        int id2 = fm.open(9);
        fm.write(id2, "Hello, from 9!");
        fm.write(id, " Update here");

        int id3 = fm.open(7);
        fm.write(id3, "vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvs");
    }
}
