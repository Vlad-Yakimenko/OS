package ua.knu.errors;

public class FileFullException extends Exception {
    public FileFullException(String err) {
        super(err);
    }

    private static final long serialVersionUID = 1L;
}
