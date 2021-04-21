package ua.knu.elements;

import ua.knu.elements.Manipulator.ByteManipulator;
import ua.knu.elements.Manipulator.Manipulator;

// DirectoryEntry represents directory entry
public class DirectoryEntry extends FSElement {
    private int name;
    private int descriptorID;
    Manipulator manipulator;

    public DirectoryEntry() {
        size = 8;
        name = 0;
        descriptorID = 0;
        manipulator = new ByteManipulator();
    }

    public FSElement deserialize(byte[] data, int pos) {
        name = manipulator.readInt(data, pos);
        descriptorID = manipulator.readInt(data, pos + 4);
        
        return this;
    }

    public void serialize(byte[] data, int pos) {
        manipulator.writeInt(data, pos, name);
        manipulator.writeInt(data, pos + 4, descriptorID);
    }

    public void setDescriptorID(int id) {
        descriptorID = id;
    }
    
    public int getDescriptorID() {
        return descriptorID;
    }

    public void setName(int name) {
        this.name = name;
    }
    
    public int getName() {
        return name;
    }
}
