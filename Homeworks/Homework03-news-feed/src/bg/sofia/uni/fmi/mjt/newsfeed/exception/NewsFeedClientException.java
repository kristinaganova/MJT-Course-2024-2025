package bg.sofia.uni.fmi.mjt.newsfeed.exception;

public class NewsFeedClientException extends Exception {
    public NewsFeedClientException(String message) {
        super(message);
    }

    public NewsFeedClientException(String message, Throwable cause) {
        super(message, cause);
    }
}