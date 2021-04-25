package ua.knu.exceptions;

public class DirectoryEntryNotFoundException extends Exception {
    public DirectoryEntryNotFoundException(String err) {
        super(err);
    }

    private static final long serialVersionUID = 1L;
}
