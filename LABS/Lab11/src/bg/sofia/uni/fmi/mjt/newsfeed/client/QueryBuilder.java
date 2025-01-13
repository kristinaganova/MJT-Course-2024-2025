package bg.sofia.uni.fmi.mjt.newsfeed.client;

import bg.sofia.uni.fmi.mjt.newsfeed.model.category.NewsFeedCategory;
import bg.sofia.uni.fmi.mjt.newsfeed.model.category.NewsFeedCountry;

import java.util.List;
import java.util.StringJoiner;

public class QueryBuilder {

    private static final String KEYWORDS = "q";
    private static final String COUNTRY = "country";
    private static final String CATEGORY = "category";
    private static final String PAGE = "page";
    private static final String PAGE_SIZE = "pageSize";

    private static final int ELEMENTS_ON_PAGE = 50;

    private final List<String> keywords;
    private final NewsFeedCategory category;
    private final NewsFeedCountry country;
    private final int page;

    public QueryBuilder(List<String> keywords, NewsFeedCategory category, NewsFeedCountry country, int page) {
        if (keywords == null || keywords.isEmpty()) {
            throw new IllegalArgumentException("At least one keyword must be provided.");
        }
        this.keywords = keywords;
        this.category = category;
        this.country = country;
        this.page = page;
    }

    public String build() {
        StringJoiner query = new StringJoiner("&", "?", "");
        addQueryParam(query, KEYWORDS, String.join(", ", keywords));
        if (country != NewsFeedCountry.DEFAULT) {
            addQueryParam(query, COUNTRY, country.country());
        }
        if (category != NewsFeedCategory.DEFAULT) {
            addQueryParam(query, CATEGORY, category.category());
        }
        addQueryParam(query, PAGE, String.valueOf(page));
        addQueryParam(query, PAGE_SIZE, String.valueOf(ELEMENTS_ON_PAGE));
        return query.toString();
    }

    private void addQueryParam(StringJoiner builder, String paramName, String paramValue) {
        if (paramValue != null && !paramValue.isEmpty()) {
            builder.add(paramName + "=" + paramValue);
        }
    }
}