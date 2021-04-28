package ua.knu;

import org.apache.commons.lang3.tuple.Pair;
import ua.knu.filesystem.FileManager;
import ua.knu.filesystem.FileManagerImpl;
import ua.knu.io.disk.Disk;
import ua.knu.io.disk.DiskInitializer;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        Disk disk = DiskInitializer.initialize();

        FileManager fm = new FileManagerImpl(disk);

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

        fm.create(5);

        fm.create(6);

        fm.create(7);

        fm.create(8);

        fm.create(9);

        //fm.create(10);

       //int id = fm.open(2);

       //fm.seek(id, 0);


       //fm.directory();
//
//       fm.remove(7);
//       fm.remove(8);
//
//       fm.create(11);
//       fm.create(12);

        //fm.create(8);
//
//        System.out.println(fm.directory());
//
//        String message = "test0_test1_test2_test3_test4_test5_test6_test7_test8_test9_test10_test11_test12_test13_test14_test15_test16_LOL";
//        System.out.println("input message length: " + message.length());
//
//        int id = fm.open(8);
//        fm.write(id, message);
//        fm.seek(id, 0);
//        byte[] data = fm.read(id, 65);
//        System.out.println(new String(data, StandardCharsets.UTF_8));
//
//        fm.remove(4);
//
//        List<Pair<?, ?>> files = fm.directory();
//        System.out.println(files);

    }
}
