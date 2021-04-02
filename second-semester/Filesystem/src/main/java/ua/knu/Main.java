package ua.knu;

import ua.knu.io.disk.DiskInitializer;
import ua.knu.io.disk.FileDisk;
import ua.knu.elements.Bitmap;
import ua.knu.elements.Manipulator.ByteManipulator;
import ua.knu.filesystem.FileManager;
import ua.knu.io.disk.Disk;

public class Main {
    public static void main(String[] args) {
        Disk disk = DiskInitializer.Initialize();

        FileManager fm = new FileManager(disk);
        fm.Create(7);
        fm.Create(12);
        fm.Create(33);
        fm.Create(22);

        fm.Remove(7);
        fm.Remove(33);

        fm.Create(11);
        fm.Create(16);
    }
}
