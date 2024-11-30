package bg.sofia.uni.fmi.mjt.glovo.exception;

public class InvalidMapEntityException extends RuntimeException {
    public InvalidMapEntityException(String message) {
        super(message);
    }

    public InvalidMapEntityException(String message, Throwable cause) {
        super(message, cause);
    }
}