package ua.knu.elements.manipulator;

public class ByteManipulator implements Manipulator {

    @Override
    public long readInt(byte[] data, int pos) {
        long result = 0;

        for (int byteID = 0; byteID < 4; byteID++) {
            result = result << 8;
            result += data[pos + byteID];
            if (data[pos + byteID] < 0) {
                result += 256;
            }
        }

        return result;
    }

    @Override
    public void writeInt(byte[] data, int pos, long value) {
        for (int byteID = 0; byteID < 4; byteID++) {
            data[pos + byteID] = (byte) (value >> (24 - byteID * 8));
        }
    }
}
