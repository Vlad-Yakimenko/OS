package ua.knu;

import ua.knu.io.disk.DiskInitializer;
import ua.knu.elements.Bitmap;
import ua.knu.filesystem.FileManager;
import ua.knu.io.disk.Disk;

public class Main {
    public static void main(String[] args) throws Exception {
        Disk disk = DiskInitializer.initialize();

        FileManager fm = new FileManager(disk);
        
        fm.create(3);
        fm.create(7);
        fm.create(9);
        int id = fm.oft.open(3);
        fm.write(id, "Hello, world!");
        int id2 = fm.oft.open(9);
        fm.write(id2, "Hello, from 9!");
        fm.write(id, " Update here");

        int id3 = fm.oft.open(7);
        fm.write(id3, "vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvs");
    }
}
