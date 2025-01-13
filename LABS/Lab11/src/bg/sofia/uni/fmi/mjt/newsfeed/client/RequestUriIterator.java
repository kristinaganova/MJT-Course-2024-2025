package bg.sofia.uni.fmi.mjt.newsfeed.client;

import java.net.URI;
import java.util.Iterator;

public class RequestUriIterator implements Iterator<URI> {

    private final int pages;
    private final NewsFeedRequest request;
    private int currentPage = 1;

    public RequestUriIterator(NewsFeedRequest request, int pages) {
        this.request = request;
        this.pages = pages;
    }

    @Override
    public boolean hasNext() {
        return currentPage <= pages;
    }

    @Override
    public URI next() {
        URI uri = request.uri(currentPage);
        currentPage++;
        return uri;
    }
}
