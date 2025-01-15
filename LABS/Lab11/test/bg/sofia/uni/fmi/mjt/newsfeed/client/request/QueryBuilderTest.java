package bg.sofia.uni.fmi.mjt.newsfeed.client.request;

import bg.sofia.uni.fmi.mjt.newsfeed.model.category.NewsFeedCategory;
import bg.sofia.uni.fmi.mjt.newsfeed.model.category.NewsFeedCountry;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QueryBuilderTest {

    @Test
    void testBuildQuery() {
        QueryBuilder queryBuilder = new QueryBuilder(
                List.of("keyword1", "keyword2"),
                NewsFeedCategory.BUSINESS,
                NewsFeedCountry.BG,
                2
        );

        String expected = "?q=keyword1,keyword2&category=business&country=bg&page=2";
        assertEquals(expected, queryBuilder.build(), "Query should be constructed correctly.");
    }
}
