package ua.knu.elements.Manipulator;

public class ByteManipulator implements Manipulator {
    @Override
    public int readInt(byte[] data, int pos) {
        int result = 0;

        for (int byteID = 0; byteID < 4; byteID++) {
            result = result << 8;
            result += (int) data[pos + byteID];
            if (data[pos + byteID] < 0) {
                result += 256;
            }
        }

        return result;
    }

    @Override
    public void writeInt(byte[] data, int pos, int value) {
        for (int byteID = 0; byteID < 4; byteID++) {
            data[pos + byteID] = (byte) (value >> (24 - byteID * 8));
        }
    }
}
