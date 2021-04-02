package ua.knu.elements;

import ua.knu.elements.Manipulator.ByteManipulator;
import ua.knu.elements.Manipulator.Manipulator;

// Bitmap represents bitmap
public class Bitmap extends FSElement {
    protected long map;
    Manipulator manipulator;

    public Bitmap() {
        size = 8;
        map = 0;
        manipulator = new ByteManipulator();
    }

    public FSElement Unmarshal(byte[] data, int pos) {
        map = manipulator.ReadInt(data, pos);
        map = map << 32;
        map += manipulator.ReadInt(data, pos + 4);
        
        return this;
    }

    // Return true when element pos is taken
    public boolean Check(int pos) {
        return (map & (((long) 1) << pos)) != 0;
    }
    
    // Set pos element as taken
    public void Set(int pos) {
        map = (map | (((long) 1) << pos));
    }

    // Set pos element as free
    public void Reset(int pos) {
        map = (map & (~(((long) 1) << pos)));
    }
    
    // Return index of free element
    // Return -1 when there are no free elements left
    public int nextFree() {
        for (int i = 7; i < 64; i++) {
            if (!Check(i)) {
                return i;
            }
        }

        return -1;
    }

    public void Marshal(byte[] data, int pos) {
        manipulator.WriteInt(data, pos, (int) map);
        manipulator.WriteInt(data, pos + 4, (int) (map >> 32));
    }

    public void SetMap(long map) {
        this.map = map;
    }
    
    public long GetMap() {
        return map;
    }
}
