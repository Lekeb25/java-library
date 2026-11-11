package exception;

public class ReaderCannotBeRemovedException extends RuntimeException  {
    public ReaderCannotBeRemovedException(String message) {
        super(message);
    }
}
