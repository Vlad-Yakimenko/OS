package ua.knu.elements.Manipulator;

public interface Manipulator {
    public int readInt(byte[] data, int pos);
    public void writeInt(byte[] data, int pos, int value);
}
