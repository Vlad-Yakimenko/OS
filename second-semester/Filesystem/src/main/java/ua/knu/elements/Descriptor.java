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

    public FSElement Unmarshal(byte[] data, int pos) {
        length = manipulator.ReadInt(data, pos);
        
        for (int i = 0; i < 3; i++) {
            blocks[i] = manipulator.ReadInt(data, pos + 4 * (i + 1));
        }

        return this;
    }

    public void Marshal(byte[] data, int pos) {
        manipulator.WriteInt(data, pos, length);

        for (int i = 0; i < 3; i++) {
            manipulator.WriteInt(data, pos + 4 * (i + 1), blocks[i]);
        }
    }

    public void SetLength(int length) {
        this.length = length;
    }
    
    public int GetLength() {
        return length;
    }

    public void SetBlocks(int[] blocks) {
        this.blocks = blocks;
    }
    
    public int[] GetBlocks() {
        return blocks;
    }
}
