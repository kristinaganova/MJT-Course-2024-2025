package bg.sofia.uni.fmi.mjt.newsfeed.client.request;

import bg.sofia.uni.fmi.mjt.newsfeed.exception.NewsFeedRequestException;
import bg.sofia.uni.fmi.mjt.newsfeed.model.category.NewsFeedCategory;
import bg.sofia.uni.fmi.mjt.newsfeed.model.category.NewsFeedCountry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class NewsFeedRequestTest {

    private static final NewsFeedCategory TEST_CATEGORY = NewsFeedCategory.BUSINESS;
    private static final NewsFeedCountry TEST_COUNTRY = NewsFeedCountry.US;
    private static final int TEST_ELEMENTS_ON_PAGE = 20;
    private static final int TEST_PAGES_COUNT = 3;
    private NewsFeedRequest request;

    @BeforeEach
    void setUp() throws NewsFeedRequestException {
        request = NewsFeedRequest.newRequest("mock")
                .forCategory(TEST_CATEGORY)
                .fromCountry(TEST_COUNTRY)
                .elementsPerPage(TEST_ELEMENTS_ON_PAGE)
                .pagesCount(TEST_PAGES_COUNT)
                .build();
    }

    @Test
    void testUriDefaultPage() {
        URI uri = request.uri();
        String expected = "https://newsapi.org/v2/top-headlines?q=mock&category=business&country=us&page=1";
        assertEquals(expected, uri.toString(), "Default URI should match the expected format.");
    }

    @Test
    void testUriSpecificPage() {
        URI uri = request.uri(2);
        String expected = "https://newsapi.org/v2/top-headlines?q=mock&category=business&country=us&page=2";
        assertEquals(expected, uri.toString(), "URI for page 2 should match the expected format.");
    }

    @Test
    void testIteratorFunctionality() {
        Iterator<URI> iterator = request.getIterator(50);
        assertNotNull(iterator, "Iterator should not be null.");
        assertTrue(iterator.hasNext(), "Iterator should have elements.");
        URI firstUri = iterator.next();
        assertEquals(request.uri(1), firstUri, "First URI should match the URI for page 1.");
    }

    @Test
    void testEqualsAndHashCode() throws NewsFeedRequestException {
        NewsFeedRequest anotherRequest = NewsFeedRequest.newRequest("mock")
                .forCategory(TEST_CATEGORY)
                .fromCountry(TEST_COUNTRY)
                .getTopNNews(60)
                .elementsPerPage(TEST_ELEMENTS_ON_PAGE)
                .pagesCount(TEST_PAGES_COUNT)
                .build();

        assertEquals(request, anotherRequest, "Requests with the same properties should be equal.");
        assertEquals(request.hashCode(), anotherRequest.hashCode(), "Hash codes of equivalent requests should match.");
    }

    @Test
    void testInvalidKeywords() {
        assertThrows(NewsFeedRequestException.class, () -> NewsFeedRequest.newRequest().build(), "Creating a request with no keywords should throw an exception.");
    }

    @Test
    void testDifferentKeywordsNotEqual() throws NewsFeedRequestException {
        NewsFeedRequest differentRequest = NewsFeedRequest.newRequest("different")
                .forCategory(TEST_CATEGORY)
                .fromCountry(TEST_COUNTRY)
                .elementsPerPage(TEST_ELEMENTS_ON_PAGE)
                .pagesCount(TEST_PAGES_COUNT)
                .build();

        assertNotEquals(request, differentRequest, "Requests with different keywords should not be equal.");
    }

    @Test
    void testDifferentCategoryNotEqual() throws NewsFeedRequestException {
        NewsFeedRequest differentRequest = NewsFeedRequest.newRequest("mock")
                .forCategory(NewsFeedCategory.HEALTH)
                .fromCountry(TEST_COUNTRY)
                .elementsPerPage(TEST_ELEMENTS_ON_PAGE)
                .pagesCount(TEST_PAGES_COUNT)
                .build();

        assertNotEquals(request, differentRequest, "Requests with different categories should not be equal.");
    }

    @Test
    void testDifferentCountryNotEqual() throws NewsFeedRequestException {
        NewsFeedRequest differentRequest = NewsFeedRequest.newRequest("mock")
                .forCategory(TEST_CATEGORY)
                .fromCountry(NewsFeedCountry.GB)
                .elementsPerPage(TEST_ELEMENTS_ON_PAGE)
                .pagesCount(TEST_PAGES_COUNT)
                .build();

        assertNotEquals(request, differentRequest, "Requests with different countries should not be equal.");
    }
}
