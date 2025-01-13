package bg.sofia.uni.fmi.mjt.newsfeed.model.datatransfer;

import java.util.List;

public record NewsFeedArticles(int totalResults, List<Article> articles) {

}
