package ua.knu.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class FilenameConverter {

    private FilenameConverter() {
        throw new IllegalStateException("Utility class");
    }

    public static int convertToInt(String filename) {
        byte[] filenameBytes = filename.getBytes(StandardCharsets.US_ASCII);
        byte[] bytes = Arrays.copyOf(filenameBytes, 4);

        ByteBuffer wrap = ByteBuffer.wrap(bytes);

        try {
            return wrap.getInt();

        } finally {
            wrap.clear();
        }
    }

    @SuppressWarnings("java:S3012")
    public static String convertToString(int filename) {
        byte[] bytes = ByteBuffer.allocate(4)
                .putInt(filename)
                .array();

        int[] integers = new int[bytes.length];

        for (int i = 0; i < bytes.length; i++) {
            integers[i] = bytes[i];
        }

        integers = Arrays.stream(integers).filter(num -> num != 0).toArray();

        bytes = new byte[integers.length];

        for (int i = 0; i < integers.length; i++) {
            bytes[i] = (byte) integers[i];
        }

        return new String(bytes);
    }
}
