package ua.knu.elements;

// FSElement represents any object stored in disk
public abstract class FSElement {
    protected int size;

    // Returns object size in bytes
    public int Size() {
        return size;
    }
    
    // Unmarshal reads object from data array starting at pos position
    public abstract FSElement Unmarshal(byte[] data, int pos);

    // Marshal saves object in data array starting at pos position
    public abstract void Marshal(byte[] data, int pos);
}