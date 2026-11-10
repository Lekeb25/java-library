package exception;

public class DocumentCannotBeRemovedException extends RuntimeException {
    public DocumentCannotBeRemovedException(String message) {
        super(message);
    }
}
