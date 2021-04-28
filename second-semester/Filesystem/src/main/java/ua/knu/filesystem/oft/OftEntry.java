package ua.knu.filesystem.oft;

import lombok.Data;

@Data
public class OFTEntry {
    private byte[] block; // This value can be null!
    private int currentPosition;
    private int descriptorPosition;
}
