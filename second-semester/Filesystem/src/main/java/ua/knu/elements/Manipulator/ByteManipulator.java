package ua.knu.elements.Manipulator;

public class ByteManipulator implements Manipulator {
    @Override
    public int ReadInt(byte[] data, int pos) {
        int result = 0;

        for (int i = 0; i < 4; i++) {
            result = result << 8;
            result += (int) data[pos + i];
        }

        return result;
    }

    @Override
    public void WriteInt(byte[] data, int pos, int value) {
        for (int i = 0; i < 4; i++) {
            data[pos + i] = (byte) (value >> (24 - i * 8));
        }
    }
}
