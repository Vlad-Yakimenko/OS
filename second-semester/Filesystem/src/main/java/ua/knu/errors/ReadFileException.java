package ua.knu.errors;

public class ReadFileException extends Exception {
    public ReadFileException(String err) {
        super(err);
    }

    private static final long serialVersionUID = 1L;
}
