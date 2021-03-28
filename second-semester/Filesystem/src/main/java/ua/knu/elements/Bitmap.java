package ua.knu.elements;

import ua.knu.elements.Manipulator.ByteManipulator;
import ua.knu.elements.Manipulator.Manipulator;

public class Bitmap extends FSElement {
    protected long map;
    Manipulator manipulator;

    public Bitmap() {
        size = 2;
        map = 0;
        manipulator = new ByteManipulator();
    }

    public FSElement Unmarshal(byte[] data, int pos) {
        map = manipulator.ReadInt(data, pos);
        map = map << 32;
        map += manipulator.ReadInt(data, pos + 4);
        
        return this;
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
