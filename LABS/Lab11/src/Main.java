import bg.sofia.uni.fmi.mjt.newsfeed.NewsFeedService;
import bg.sofia.uni.fmi.mjt.newsfeed.client.NewsFeedHttpClient;
import bg.sofia.uni.fmi.mjt.newsfeed.client.NewsFeedRequest;
import bg.sofia.uni.fmi.mjt.newsfeed.model.category.NewsFeedResult;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.NewsFeedClientException;
import bg.sofia.uni.fmi.mjt.newsfeed.cache.TimedCache;

import java.net.http.HttpClient;

public class Main {

    public static void main(String... args) throws NewsFeedClientException {
        final int timeout = 20;
        NewsFeedHttpClient httpClient = new NewsFeedHttpClient(HttpClient.newHttpClient());
        TimedCache<NewsFeedRequest, NewsFeedResult> cache = new TimedCache<>(timeout);
        NewsFeedService client = new NewsFeedService(httpClient, cache);
        var result = client.execute(NewsFeedRequest.newRequest("tesla").build());
    }
}
