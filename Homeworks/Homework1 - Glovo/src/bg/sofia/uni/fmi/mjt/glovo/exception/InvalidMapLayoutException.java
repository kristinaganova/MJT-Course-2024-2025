package bg.sofia.uni.fmi.mjt.glovo.exception;

public class InvalidMapLayoutException extends RuntimeException {
    public InvalidMapLayoutException(String message) {
        super(message);
    }

    public InvalidMapLayoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
