package bg.sofia.uni.fmi.mjt.newsfeed.client;

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
    private final int elementsOnPage;  // Позволява задаване на брой елементи на страница
    private final int pagesCount;  // Позволява задаване на брой страници

    public static final String API_ENDPOINT_SCHEME = "https";
    public static final String API_ENDPOINT_HOST = "newsapi.org";
    public static final String API_ENDPOINT_PATH = "/v2/top-headlines";

    public static final String KEYWORDS_PARAM = "q";
    public static final String COUNTRY_PARAM = "country";
    public static final String CATEGORY_PARAM = "category";
    public static final String PAGE_PARAM = "page";
    public static final String PAGE_SIZE_PARAM = "pageSize";

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

        return URI.create(API_ENDPOINT_SCHEME + "://" + API_ENDPOINT_HOST + API_ENDPOINT_PATH + query);
    }

    public Iterator<URI> getIterator(int elementsTotal) {
        elementsTotal = Math.min(desiredResultsCount, elementsTotal);
        return new RequestUriIterator(this, Math.ceilDiv(elementsTotal, elementsOnPage));
    }

    public static class NewsFeedRequestBuilder {
        private final List<String> keywords;
        private NewsFeedCategory category = NewsFeedCategory.DEFAULT;
        private NewsFeedCountry country = NewsFeedCountry.DEFAULT;
        private int desiredCount = DEFAULT_RESULTS_COUNT;
        private int elementsOnPage = DEFAULT_ELEMENTS_ON_PAGE;
        private int pagesCount = DEFAULT_PAGES_COUNT;

        protected NewsFeedRequestBuilder(String... keywords) {
            this.keywords = List.of(keywords);
        }

        public List<String> getKeywords() {
            return keywords;
        }

        public NewsFeedCategory getCategory() {
            return category;
        }

        public int getDesiredCount() {
            return desiredCount;
        }

        public NewsFeedCountry getCountry() {
            return country;
        }

        public int getElementsOnPage() {
            return elementsOnPage;
        }

        public int getPagesCount() {
            return pagesCount;
        }

        public NewsFeedRequestBuilder forCategory(NewsFeedCategory category) {
            this.category = category;
            return this;
        }

        public NewsFeedRequestBuilder fromCountry(NewsFeedCountry country) {
            this.country = country;
            return this;
        }

        public NewsFeedRequestBuilder getTopNNews(int n) {
            if (n > 0) {
                desiredCount = n;
            }
            return this;
        }

        public NewsFeedRequestBuilder elementsPerPage(int count) {
            if (count > 0) {
                elementsOnPage = count;
            }
            return this;
        }

        public NewsFeedRequestBuilder pagesCount(int count) {
            if (count > 0) {
                pagesCount = count;
            }
            return this;
        }

        public NewsFeedRequest build() {
            if (keywords.isEmpty()) {
                throw new IllegalArgumentException("At least one keyword must be provided.");
            }
            if (category == NewsFeedCategory.DEFAULT) {
                throw new IllegalArgumentException("Category must be specified.");
            }
            if (country == NewsFeedCountry.DEFAULT) {
                throw new IllegalArgumentException("Country must be specified.");
            }
            return new NewsFeedRequest(this);
        }
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
