package ua.knu.elements;

import ua.knu.elements.Manipulator.ByteManipulator;
import ua.knu.elements.Manipulator.Manipulator;

//Descriptor represents file descroptor
public class Descriptor extends FSElement {
    private int length;
    private int[] blocks;
    Manipulator manipulator;

    public Descriptor() {
        size = 16;
        blocks = new int[3];
        length = 0;
        manipulator = new ByteManipulator();
    }
    
    public FSElement deserialize(byte[] data, int pos) {
        length = manipulator.readInt(data, pos);
        if (length > (((long) 1) << 15)) {
            length = -1;
        }
        
        for (int i = 0; i < 3; i++) {
            blocks[i] = manipulator.readInt(data, pos + 4 * (i + 1));
        }

        return this;
    }

    public void serialize(byte[] data, int pos) {
        manipulator.writeInt(data, pos, length);

        for (int i = 0; i < 3; i++) {
            manipulator.writeInt(data, pos + 4 * (i + 1), blocks[i]);
        }
    }

    public void setLength(int length) {
        this.length = length;
    }
    
    public int getLength() {
        return length;
    }

    public void setBlocks(int[] blocks) {
        this.blocks = blocks;
    }
    
    public int[] getBlocks() {
        return blocks;
    }
}
