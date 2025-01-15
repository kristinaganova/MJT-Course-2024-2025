package bg.sofia.uni.fmi.mjt.newsfeed.exception;

public class NewsFeedRequestException extends Exception {
    public NewsFeedRequestException(String message) {
        super(message);
    }

    public NewsFeedRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
