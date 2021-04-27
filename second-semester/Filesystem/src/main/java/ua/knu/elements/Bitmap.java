package ua.knu.elements;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ua.knu.elements.manipulator.ByteManipulator;
import ua.knu.elements.manipulator.Manipulator;

// Bitmap represents bitmap
@Data
@EqualsAndHashCode(callSuper=true)
public class Bitmap extends FSElement {
    private long map;
    private final Manipulator manipulator;

    public Bitmap() {
        size = 8;
        map = 1;
        manipulator = new ByteManipulator();
    }

    public FSElement deserialize(byte[] data, int pos) {
        map = manipulator.readInt(data, pos);
        map = map << 32;
        map += manipulator.readInt(data, pos + 4);
        
        return this;
    }

    // Return true when element pos is taken
    public boolean check(int pos) {
        return (map & (((long) 1) << pos)) != 0;
    }
    
    // set pos element as taken
    public void set(int pos) {
        map = (map | (((long) 1) << pos));
    }

    // set pos element as free
    public void reset(int pos) {
        map = (map & (~(((long) 1) << pos)));
    }
    
    // Return index of free element
    // Return -1 when there are no free elements left
    public int nextFree() {
        for (int block = 7; block < 64; block++) {
            if (!check(block)) {
                return block;
            }
        }

        return -1;
    }

    public void serialize(byte[] data, int pos) {
        manipulator.writeInt(data, pos, (int) (map >> 32));
        manipulator.writeInt(data, pos + 4, (int) map);
    }
}
