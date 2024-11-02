package bg.sofia.uni.fmi.mjt.socialnetwork.post;

public class PostIdGenerator {
    private static long counter = 0;

    public static String generateId() {
        return "POST-" + (++counter);
    }
}