package bg.sofia.uni.fmi.mjt.newsfeed.client.request;

import bg.sofia.uni.fmi.mjt.newsfeed.model.category.NewsFeedCategory;
import bg.sofia.uni.fmi.mjt.newsfeed.model.category.NewsFeedCountry;

import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class NewsFeedRequest {

    private final int desiredResultsCount;
    private final List<String> keywords;
    private final NewsFeedCategory category;
    private final NewsFeedCountry country;
    private final int elementsOnPage;
    private final int pagesCount;

    public static final String ENDPOINT_SCHEME = "https";
    public static final String ENDPOINT_HOST = "newsapi.org";
    public static final String ENDPOINT_PATH = "/v2/top-headlines";

    public static final int DEFAULT_ELEMENTS_ON_PAGE = 50;
    public static final int DEFAULT_PAGES_COUNT = 2;

    public static final int DEFAULT_PAGE_NUMBER = 1;
    public static final int DEFAULT_RESULTS_COUNT = DEFAULT_PAGES_COUNT * DEFAULT_ELEMENTS_ON_PAGE;

    protected NewsFeedRequest(NewsFeedRequestBuilder builder) {
        this.keywords = builder.getKeywords();
        this.category = builder.getCategory();
        this.country = builder.getCountry();
        this.desiredResultsCount = builder.getDesiredCount();
        this.elementsOnPage = builder.getElementsOnPage();
        this.pagesCount = builder.getPagesCount();
    }

    public static NewsFeedRequestBuilder newRequest(String... keywords) {
        return new NewsFeedRequestBuilder(keywords);
    }

    public URI uri() {
        return uri(DEFAULT_PAGE_NUMBER);
    }

    public URI uri(int page) {
        QueryBuilder queryBuilder = new QueryBuilder(keywords, category, country, page);

        String query = queryBuilder.build();

        return URI.create(ENDPOINT_SCHEME + "://" + ENDPOINT_HOST + ENDPOINT_PATH + query);
    }

    public Iterator<URI> getIterator(int elementsTotal) {
        elementsTotal = Math.min(desiredResultsCount, elementsTotal);
        return new RequestUriIterator(this, Math.ceilDiv(elementsTotal, elementsOnPage));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewsFeedRequest that = (NewsFeedRequest) o;
        return keywords.equals(that.keywords) && category == that.category && country == that.country;
    }

    @Override
    public int hashCode() {
        return Objects.hash(keywords, category, country);
    }
}