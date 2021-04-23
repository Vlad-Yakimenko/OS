package ua.knu.errors;

public class FileNotFoundException extends Exception {
    public FileNotFoundException(String err) {
        super(err);
    }

    private static final long serialVersionUID = 1L;
}
