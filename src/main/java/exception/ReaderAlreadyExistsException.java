package exception;

public class ReaderAlreadyExistsException extends RuntimeException {
    public ReaderAlreadyExistsException(String message) {
        super(message);
    }
}
