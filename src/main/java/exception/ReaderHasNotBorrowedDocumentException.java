package exception;

public class ReaderHasNotBorrowedDocumentException extends RuntimeException {
    public ReaderHasNotBorrowedDocumentException(String message) {

        super(message);
    }
}
