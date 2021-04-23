package ua.knu.errors;

public class DirectoryFullException extends Exception {
    public DirectoryFullException(String err) {
        super(err);
    }

    private static final long serialVersionUID = 1L;
}
