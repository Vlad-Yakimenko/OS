package ua.knu.elements.manipulator;

public interface Manipulator {
    int readInt(byte[] data, int pos);
    void writeInt(byte[] data, int pos, int value);
}
