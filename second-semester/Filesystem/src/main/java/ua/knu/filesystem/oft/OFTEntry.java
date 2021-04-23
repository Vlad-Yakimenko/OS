package ua.knu.filesystem.oft;

import lombok.Data;

@Data
class OFTEntry {
    // This value can be null!
    private byte[] block;
    private int currentPosition;
    private int descriptorPosition;
}
