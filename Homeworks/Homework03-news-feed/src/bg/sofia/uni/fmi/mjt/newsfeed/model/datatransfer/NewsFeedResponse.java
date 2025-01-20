package bg.sofia.uni.fmi.mjt.newsfeed.model.datatransfer;

import java.util.List;

//TODO:
// add NewsFeedResponseException
public record NewsFeedResponse(String status, String code, String message, int totalResults, List<Article> articles) { }

