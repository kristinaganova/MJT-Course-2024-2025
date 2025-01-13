package bg.sofia.uni.fmi.mjt.newsfeed.client;

import bg.sofia.uni.fmi.mjt.newsfeed.exception.NewsFeedClientException;
import bg.sofia.uni.fmi.mjt.newsfeed.model.datatransfer.NewsFeedArticles;

import java.net.URI;

public interface NewsFeedClient {
    NewsFeedArticles getNewsFeed(URI requestUri) throws NewsFeedClientException;
}
