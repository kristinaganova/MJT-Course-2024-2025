package bg.sofia.uni.fmi.mjt.newsfeed.client.request;

import bg.sofia.uni.fmi.mjt.newsfeed.exception.NewsFeedRequestException;
import bg.sofia.uni.fmi.mjt.newsfeed.model.category.NewsFeedCategory;
import bg.sofia.uni.fmi.mjt.newsfeed.model.category.NewsFeedCountry;

import java.util.List;

public class NewsFeedRequestBuilder {

    private final List<String> keywords;
    private NewsFeedCategory category = NewsFeedCategory.DEFAULT;
    private NewsFeedCountry country = NewsFeedCountry.DEFAULT;
    private int desiredCount = NewsFeedRequest.DEFAULT_RESULTS_COUNT;
    private int elementsOnPage = NewsFeedRequest.DEFAULT_ELEMENTS_ON_PAGE;
    private int pagesCount = NewsFeedRequest.DEFAULT_PAGES_COUNT;

    public NewsFeedRequestBuilder(String... keywords) throws NewsFeedRequestException {
        if (keywords == null || keywords.length == 0) {
            throw new NewsFeedRequestException("At least one keyword must be provided.");
        }
        this.keywords = List.of(keywords);
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public NewsFeedCategory getCategory() {
        return category;
    }

    public NewsFeedCountry getCountry() {
        return country;
    }

    public int getDesiredCount() {
        return desiredCount;
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

    public NewsFeedRequestBuilder getTopNNews(int count) {
        if (count > 0) {
            this.desiredCount = count;
        }
        return this;
    }

    public NewsFeedRequestBuilder elementsPerPage(int count) {
        if (count > 0) {
            this.elementsOnPage = count;
        }
        return this;
    }

    public NewsFeedRequestBuilder pagesCount(int count) {
        if (count > 0) {
            this.pagesCount = count;
        }
        return this;
    }

    public NewsFeedRequest build() throws NewsFeedRequestException {
        if (keywords.isEmpty() && (category != NewsFeedCategory.DEFAULT || country != NewsFeedCountry.DEFAULT)) {
            throw new NewsFeedRequestException("Invalid request: Keywords are mandatory when specifying " +
                    "                           category or country.");
        }
        return new NewsFeedRequest(this);
    }
}
