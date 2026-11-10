package exception;

import java.io.IOException;

public class InvalidSaveFileException extends IOException {
    public InvalidSaveFileException(String message) {
        super(message);
    }

    public InvalidSaveFileException(String message, Throwable reason) { super(message, reason);}
}
