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

    public FSElement Unmarshal(byte[] data, int pos) {
        name = manipulator.ReadInt(data, pos);
        descriptorID = manipulator.ReadInt(data, pos + 4);
        
        return this;
    }

    public void Marshal(byte[] data, int pos) {
        manipulator.WriteInt(data, pos, name);
        manipulator.WriteInt(data, pos + 4, descriptorID);
    }

    public void SetDescriptorID(int id) {
        descriptorID = id;
    }
    
    public int GetDescriptorID() {
        return descriptorID;
    }

    public void SetName(int name) {
        this.name = name;
    }
    
    public int GetName() {
        return name;
    }
}
