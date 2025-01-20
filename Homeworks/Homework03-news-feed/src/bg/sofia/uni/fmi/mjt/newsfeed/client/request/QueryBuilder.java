package bg.sofia.uni.fmi.mjt.newsfeed.client.request;

import bg.sofia.uni.fmi.mjt.newsfeed.model.category.NewsFeedCategory;
import bg.sofia.uni.fmi.mjt.newsfeed.model.category.NewsFeedCountry;

import java.util.List;

class QueryBuilder {

    private final List<String> keywords;
    private final NewsFeedCategory category;
    private final NewsFeedCountry country;
    private final int page;

    public QueryBuilder(List<String> keywords, NewsFeedCategory category, NewsFeedCountry country, int page) {
        this.keywords = keywords;
        this.category = category;
        this.country = country;
        this.page = page;
    }

    public String build() {
        StringBuilder query = new StringBuilder("?");

        if (!keywords.isEmpty()) {
            query.append("q=").append(String.join(",", keywords)).append("&");
        }
        if (category != NewsFeedCategory.DEFAULT) {
            query.append("category=").append(category.name().toLowerCase()).append("&");
        }
        if (country != NewsFeedCountry.DEFAULT) {
            query.append("country=").append(country.name().toLowerCase()).append("&");
        }
        query.append("page=").append(page);

        return query.toString();
    }
}