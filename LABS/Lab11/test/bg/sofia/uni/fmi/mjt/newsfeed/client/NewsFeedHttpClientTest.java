package bg.sofia.uni.fmi.mjt.newsfeed.client;

import bg.sofia.uni.fmi.mjt.newsfeed.model.datatransfer.NewsFeedArticles;
import bg.sofia.uni.fmi.mjt.newsfeed.model.datatransfer.NewsFeedResponse;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.NewsFeedClientException;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NewsFeedHttpClientTest {

    private static final String TEST_API_KEY = "test-api-key";
    private static final String TEST_URI = "https://newsapi.org/v2/top-headlines";
    private static final String RESPONSE_BODY_OK = "{\"status\":\"ok\",\"totalResults\":10,\"articles\":[]}";
    private static final String RESPONSE_BODY_ERROR = "{\"status\":\"error\",\"code\":\"test-code\",\"message\":\"test-message\"}";

    private HttpClient mockHttpClient;
    private HttpResponse<String> mockHttpResponse;
    private NewsFeedHttpClient newsFeedHttpClient;

    @BeforeEach
    void setUp() {
        mockHttpClient = mock(HttpClient.class);
        mockHttpResponse = mock(HttpResponse.class);
        newsFeedHttpClient = new NewsFeedHttpClient(mockHttpClient, TEST_API_KEY);
    }

    @Test
    void testGetNewsFeedReturnsArticlesOnOkResponse() throws Exception {
        when(mockHttpResponse.body()).thenReturn(RESPONSE_BODY_OK);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(mockHttpResponse);

        URI requestUri = URI.create(TEST_URI);
        NewsFeedArticles articles = newsFeedHttpClient.getNewsFeed(requestUri);

        assertNotNull(articles, "NewsFeedArticles should not be null on a successful response.");
        assertEquals(10, articles.totalResults(), "Total results should match the response.");
        assertTrue(articles.articles().isEmpty(), "Articles list should be empty as per the response.");
    }

    @Test
    void testGetNewsFeedThrowsExceptionOnErrorResponse() throws Exception {
        when(mockHttpResponse.body()).thenReturn(RESPONSE_BODY_ERROR);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(mockHttpResponse);

        URI requestUri = URI.create(TEST_URI);

        NewsFeedClientException exception = assertThrows(NewsFeedClientException.class, () -> newsFeedHttpClient.getNewsFeed(requestUri),
                "Expected an exception for error response");
        assertTrue(exception.getMessage().contains("Error code: test-code"), "Exception message should contain error details.");
    }

    @Test
    void testGetNewsFeedThrowsExceptionOnRequestFailure() throws Exception {
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenThrow(new RuntimeException("Request failed"));

        URI requestUri = URI.create(TEST_URI);

        NewsFeedClientException exception = assertThrows(NewsFeedClientException.class, () -> newsFeedHttpClient.getNewsFeed(requestUri),
                "Expected an exception for request failure");
        assertTrue(exception.getMessage().contains("Could not retrieve news feed."), "Exception message should indicate request failure.");
    }

    @Test
    void testGetNewsFeedThrowsExceptionOnNullResponseBody() throws Exception {
        when(mockHttpResponse.body()).thenReturn(null);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(mockHttpResponse);

        URI requestUri = URI.create(TEST_URI);

        NewsFeedClientException exception = assertThrows(NewsFeedClientException.class, () -> newsFeedHttpClient.getNewsFeed(requestUri),
                "Expected an exception for null response body");
        assertTrue(exception.getMessage().contains("Parsed response is null."), "Exception message should indicate null response body.");
    }
}
