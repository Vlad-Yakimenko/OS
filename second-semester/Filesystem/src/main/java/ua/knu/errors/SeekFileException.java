package ua.knu.errors;

public class SeekFileException extends Exception {
    public SeekFileException(String err) {
        super(err);
    }

    private static final long serialVersionUID = 1L;
}
