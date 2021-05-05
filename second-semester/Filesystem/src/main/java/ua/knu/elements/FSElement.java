package ua.knu.elements;

// FSElement represents any object stored in disk
public abstract class FSElement {
    protected int size;

    // Returns object size in bytes
    public int size() {
        return size;
    }
    
    // deserialize reads object from data array starting at pos position
    public abstract FSElement deserialize(byte[] data, int pos);

    // serialize saves object in data array starting at pos position
    public abstract void serialize(byte[] data, int pos);
}
