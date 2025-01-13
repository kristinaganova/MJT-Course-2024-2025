package bg.sofia.uni.fmi.mjt.newsfeed.model.category;

import bg.sofia.uni.fmi.mjt.newsfeed.model.datatransfer.Article;

import java.util.List;

public record NewsFeedResult(List<Article> articles) {
}
