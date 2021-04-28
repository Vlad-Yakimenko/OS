package ua.knu.filesystem.oft;

import lombok.Data;

@Data
public class OFTEntry {
    // This value can be null!
    private byte[] block;
    private int currentPosition;
    private int descriptorPosition;
}
