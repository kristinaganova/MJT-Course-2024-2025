package bg.sofia.uni.fmi.mjt.newsfeed.exception;

public class NewsFeedResponseException extends Exception {
    public NewsFeedResponseException(String message) {
        super(message);
    }

    public NewsFeedResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
