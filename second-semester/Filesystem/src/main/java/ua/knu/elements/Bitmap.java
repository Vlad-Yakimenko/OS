package ua.knu.elements;

import ua.knu.elements.Manipulator.ByteManipulator;
import ua.knu.elements.Manipulator.Manipulator;

// Bitmap represents bitmap
public class Bitmap extends FSElement {
    protected long map;
    Manipulator manipulator;

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
        for (int i = 7; i < 64; i++) {
            if (!check(i)) {
                return i;
            }
        }

        return -1;
    }

    public void serialize(byte[] data, int pos) {
        manipulator.writeInt(data, pos, (int) (map >> 32));
        manipulator.writeInt(data, pos + 4, (int) map);
    }

    public void setMap(long map) {
        this.map = map;
    }
    
    public long getMap() {
        return map;
    }
}
