package bg.sofia.uni.fmi.mjt.newsfeed.client.request;

import java.net.URI;
import java.util.Iterator;

class RequestUriIterator implements Iterator<URI> {

    private final NewsFeedRequest request;
    private final int totalPages;
    private int currentPage = 1;

    public RequestUriIterator(NewsFeedRequest request, int totalPages) {
        this.request = request;
        this.totalPages = totalPages;
    }

    @Override
    public boolean hasNext() {
        return currentPage <= totalPages;
    }

    @Override
    public URI next() {
        if (!hasNext()) {
            throw new IllegalArgumentException("No more pages");
        }
        return request.uri(currentPage++);
    }
}