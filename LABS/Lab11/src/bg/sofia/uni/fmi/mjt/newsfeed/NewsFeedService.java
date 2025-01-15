package bg.sofia.uni.fmi.mjt.newsfeed;

import bg.sofia.uni.fmi.mjt.newsfeed.client.NewsFeedHttpClient;
import bg.sofia.uni.fmi.mjt.newsfeed.client.request.NewsFeedRequest;
import bg.sofia.uni.fmi.mjt.newsfeed.model.category.NewsFeedResult;
import bg.sofia.uni.fmi.mjt.newsfeed.model.datatransfer.Article;
import bg.sofia.uni.fmi.mjt.newsfeed.model.datatransfer.NewsFeedArticles;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.NewsFeedClientException;
import bg.sofia.uni.fmi.mjt.newsfeed.cache.Cache;

import java.util.ArrayList;
import java.util.List;

public class NewsFeedService {
    private final NewsFeedHttpClient client;
    private final Cache<NewsFeedRequest, NewsFeedResult> cache;

    public NewsFeedService(NewsFeedHttpClient client, Cache<NewsFeedRequest, NewsFeedResult> cache) {
        this.client = client;
        this.cache = cache;
    }

    public NewsFeedResult execute(NewsFeedRequest request) throws NewsFeedClientException {
        NewsFeedResult cachedResult = cache.get(request);
        if (cachedResult != null) {
            return cachedResult;
        }

        try {
            NewsFeedArticles articles = client.getNewsFeed(request.uri());
            List<Article> allArticles = new ArrayList<>(articles.articles());

            var iterator = request.getIterator(articles.totalResults());
            while (iterator.hasNext()) {
                allArticles.addAll(client.getNewsFeed(iterator.next()).articles());
            }

            NewsFeedResult result = new NewsFeedResult(allArticles);
            cache.put(request, result);

            return result;
        } catch (NewsFeedClientException e) {
            throw new NewsFeedClientException("Error fetching news feed: " + e.getMessage(), e);
        }
    }
}
