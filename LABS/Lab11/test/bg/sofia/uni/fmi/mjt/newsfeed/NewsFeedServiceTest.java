package bg.sofia.uni.fmi.mjt.newsfeed;

import bg.sofia.uni.fmi.mjt.newsfeed.client.NewsFeedHttpClient;
import bg.sofia.uni.fmi.mjt.newsfeed.client.request.NewsFeedRequest;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.NewsFeedResponseException;
import bg.sofia.uni.fmi.mjt.newsfeed.model.category.NewsFeedResult;
import bg.sofia.uni.fmi.mjt.newsfeed.model.datatransfer.Article;
import bg.sofia.uni.fmi.mjt.newsfeed.model.datatransfer.ArticleSource;
import bg.sofia.uni.fmi.mjt.newsfeed.model.datatransfer.NewsFeedArticles;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.NewsFeedClientException;
import bg.sofia.uni.fmi.mjt.newsfeed.cache.Cache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NewsFeedServiceTest {

    private NewsFeedHttpClient mockClient;
    private Cache<NewsFeedRequest, NewsFeedResult> mockCache;
    private NewsFeedService newsFeedService;
    private NewsFeedRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockClient = mock(NewsFeedHttpClient.class);
        mockCache = mock(Cache.class);
        newsFeedService = new NewsFeedService(mockClient, mockCache);

        mockRequest = mock(NewsFeedRequest.class);
        when(mockRequest.uri()).thenReturn(URI.create("https://example.com/news"));
    }

    @Test
    void testExecuteReturnsCachedResult() throws NewsFeedClientException, NewsFeedResponseException {
        NewsFeedResult cachedResult = new NewsFeedResult(Collections.emptyList());
        when(mockCache.get(mockRequest)).thenReturn(cachedResult);

        NewsFeedResult result = newsFeedService.execute(mockRequest);

        verify(mockCache, times(1)).get(mockRequest);
        verify(mockClient, never()).getNewsFeed(any());
        assertEquals(cachedResult, result, "The result should be fetched from the cache.");
    }

    @Test
    void testExecuteFetchesAndCachesResult() throws NewsFeedClientException, NewsFeedResponseException {
        when(mockCache.get(mockRequest)).thenReturn(null);

        List<Article> articles = List.of(new Article(new ArticleSource("Unique ID", "Test Source"), "Test Author", "Test Title", "Test Description", "https://example.com", null, null, null));
        NewsFeedArticles newsFeedArticles = new NewsFeedArticles(1, articles);

        when(mockClient.getNewsFeed(mockRequest.uri())).thenReturn(newsFeedArticles);
        Iterator<URI> mockIterator = mock(Iterator.class);
        when(mockIterator.hasNext()).thenReturn(false);
        when(mockRequest.getIterator(1)).thenReturn(mockIterator);

        NewsFeedResult result = newsFeedService.execute(mockRequest);

        verify(mockCache, times(1)).put(eq(mockRequest), any(NewsFeedResult.class));
        assertNotNull(result, "Result should not be null.");
        assertEquals(articles, result.articles(), "Result should contain the fetched articles.");
    }

    @Test
    void testExecuteHandlesClientException() throws NewsFeedClientException, NewsFeedResponseException {
        when(mockCache.get(mockRequest)).thenReturn(null);
        when(mockClient.getNewsFeed(mockRequest.uri())).thenThrow(new NewsFeedClientException("Test exception"));

        NewsFeedClientException exception = assertThrows(NewsFeedClientException.class, () -> newsFeedService.execute(mockRequest),
                "Expected a NewsFeedClientException to be thrown.");
        assertTrue(exception.getMessage().contains("Error fetching news feed"), "Exception message should indicate the cause.");
    }
}