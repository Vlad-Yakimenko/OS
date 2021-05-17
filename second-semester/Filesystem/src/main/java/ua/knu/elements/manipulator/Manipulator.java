package ua.knu.elements.manipulator;

public interface Manipulator {
    long readInt(byte[] data, int pos);
    void writeInt(byte[] data, int pos, long value);
}
