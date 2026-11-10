package exception;

public class ReaderAlreadyBorrowedDocumentException extends RuntimeException {
    public ReaderAlreadyBorrowedDocumentException(String message) {
        super(message);
    }
}
