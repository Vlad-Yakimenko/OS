package ua.knu;

import ua.knu.io.disk.FileDisk;

public class Main {
    public static void main(String[] args) {
        FileDisk disk = new FileDisk("disk.bin", 64, 64);
        disk.Init();
    }
}
