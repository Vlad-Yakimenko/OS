package ua.knu.elements.Manipulator;

public interface Manipulator {
    public int ReadInt(byte[] data, int pos);
    public void WriteInt(byte[] data, int pos, int value);
}
