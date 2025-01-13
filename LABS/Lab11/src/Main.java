
import bg.sofia.uni.fmi.mjt.newsfeed.NewsFeedService;
import bg.sofia.uni.fmi.mjt.newsfeed.client.NewsFeedHttpClient;
import bg.sofia.uni.fmi.mjt.newsfeed.client.request.NewsFeedRequest;
import bg.sofia.uni.fmi.mjt.newsfeed.model.category.NewsFeedResult;
import bg.sofia.uni.fmi.mjt.newsfeed.model.datatransfer.Article;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.NewsFeedClientException;
import bg.sofia.uni.fmi.mjt.newsfeed.cache.TimedCache;

import java.net.http.HttpClient;

public class Main {

    public static void main(String... args) {
        final int timeout = 20;

        try {
            NewsFeedHttpClient httpClient = new NewsFeedHttpClient(HttpClient.newHttpClient());
            TimedCache<NewsFeedRequest, NewsFeedResult> cache = new TimedCache<>(timeout);
            NewsFeedService client = new NewsFeedService(httpClient, cache);

            NewsFeedRequest request = NewsFeedRequest.newRequest("London").build();
            NewsFeedResult result = client.execute(request);

            if (result != null && result.articles() != null) {
                System.out.println("News Feed Results:");
                for (Article article : result.articles()) {
                    System.out.println("-----------------------------------");
                    System.out.println("Title: " + article.title());
                    System.out.println("Author: " + article.author());
                    System.out.println("Description: " + article.description());
                    System.out.println("URL: " + article.url());
                    System.out.println("Published At: " + article.publishedAt());
                    System.out.println("-----------------------------------");
                }
            } else {
                System.out.println("No results found for the given query.");
            }

        } catch (NewsFeedClientException e) {
            System.err.println("Error retrieving news feed: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }
}
