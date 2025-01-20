package bg.sofia.uni.fmi.mjt.newsfeed.client.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RequestUriIteratorTest {

    private NewsFeedRequest mockRequest;
    private RequestUriIterator iterator;
    private static final int TOTAL_PAGES = 3;

    @BeforeEach
    void setUp() {
        mockRequest = mock(NewsFeedRequest.class);
        iterator = new RequestUriIterator(mockRequest, TOTAL_PAGES);
    }

    @Test
    void testHasNextInitiallyTrue() {
        assertTrue(iterator.hasNext(), "Iterator should have next pages initially.");
    }

    @Test
    void testHasNextAfterAllPages() {
        for (int i = 1; i <= TOTAL_PAGES; i++) {
            when(mockRequest.uri(i)).thenReturn(URI.create("https://example.com/page=" + i));
            iterator.next();
        }
        assertFalse(iterator.hasNext(), "Iterator should not have next pages after iterating through all pages.");
    }

    @Test
    void testNextReturnsCorrectUris() {
        for (int i = 1; i <= TOTAL_PAGES; i++) {
            URI expectedUri = URI.create("https://example.com/page=" + i);
            when(mockRequest.uri(i)).thenReturn(expectedUri);

            URI actualUri = iterator.next();
            assertEquals(expectedUri, actualUri, "Iterator should return the correct URI for page " + i);
        }
    }

    @Test
    void testNextThrowsExceptionWhenNoNextPage() {
        for (int i = 1; i <= TOTAL_PAGES; i++) {
            when(mockRequest.uri(i)).thenReturn(URI.create("https://example.com/page=" + i));
            iterator.next();
        }
        assertThrows(
                IllegalArgumentException.class,
                iterator::next,
                "Calling next() when no pages are left should throw NoSuchElementException."
        );
    }
}
