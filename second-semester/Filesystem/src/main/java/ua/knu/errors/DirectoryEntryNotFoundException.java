package ua.knu.errors;

public class DirectoryEntryNotFoundException extends Exception {
    public DirectoryEntryNotFoundException(String err) {
        super(err);
    }
}
