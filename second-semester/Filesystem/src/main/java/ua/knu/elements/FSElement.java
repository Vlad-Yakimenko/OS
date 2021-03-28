package ua.knu.elements;

public abstract class FSElement {
    protected int size;
    public int Size() {
        return size;
    }

    public abstract FSElement Unmarshal(byte[] data, int pos);
    public abstract void Marshal(byte[] data, int pos);
}