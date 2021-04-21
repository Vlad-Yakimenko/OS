package ua.knu.elements.Manipulator;

public class ByteManipulator implements Manipulator {
    @Override
    public int readInt(byte[] data, int pos) {
        int result = 0;

        for (int i = 0; i < 4; i++) {
            result = result << 8;
            result += (int) data[pos + i];
            if (data[pos + i] < 0) {
                result += 256;
            }
        }

        return result;
    }

    @Override
    public void writeInt(byte[] data, int pos, int value) {
        for (int i = 0; i < 4; i++) {
            data[pos + i] = (byte) (value >> (24 - i * 8));
        }
    }
}
