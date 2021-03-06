package ua.knu.elements;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ua.knu.elements.manipulator.ByteManipulator;
import ua.knu.elements.manipulator.Manipulator;

//Descriptor represents file descriptor
@Data
@EqualsAndHashCode(callSuper=true)
public class Descriptor extends FSElement {
    private int length;
    private int[] blocks;
    private final Manipulator manipulator;

    public Descriptor() {
        size = 16;
        blocks = new int[3];
        length = 0;
        manipulator = new ByteManipulator();
    }
    
    public FSElement deserialize(byte[] data, int pos) {
        length = (int) manipulator.readInt(data, pos);
        if (length > (((long) 1) << 15)) {
            length = -1;
        }
        
        for (int blockID = 0; blockID < blocks.length; blockID++) {
            blocks[blockID] = (int) manipulator.readInt(data, pos + 4 * (blockID + 1));
        }

        return this;
    }

    public void serialize(byte[] data, int pos) {
        manipulator.writeInt(data, pos, length);

        for (int blockID = 0; blockID < blocks.length; blockID++) {
            manipulator.writeInt(data, pos + 4 * (blockID + 1), blocks[blockID]);
        }
    }
}
