package bg.sofia.uni.fmi.mjt.newsfeed.client;

import bg.sofia.uni.fmi.mjt.newsfeed.model.datatransfer.NewsFeedArticles;
import bg.sofia.uni.fmi.mjt.newsfeed.model.datatransfer.NewsFeedResponse;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.NewsFeedClientException;
import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class NewsFeedHttpClient implements NewsFeedClient {

    private static final String API_KEY = "06b6f456051244fe854c0286a3a792f5";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String OK_STATUS = "ok";
    private static final String ERROR_STATUS = "error";
    private static final Gson GSON = new Gson();

    private final HttpClient httpClient;
    private final String apiKey;

    public NewsFeedHttpClient(HttpClient httpClient) {
        this(httpClient, API_KEY);
    }

    public NewsFeedHttpClient(HttpClient httpClient, String apiKey) {
        this.httpClient = httpClient;
        this.apiKey = apiKey;
    }

    @Override
    public NewsFeedArticles getNewsFeed(URI requestUri) throws NewsFeedClientException {
        String responseBody = sendRequest(requestUri);
        NewsFeedResponse response = parseResponse(responseBody);

        validateResponse(response);

        return new NewsFeedArticles(response.totalResults(), response.articles());
    }

    private String sendRequest(URI requestUri) throws NewsFeedClientException {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(requestUri)
                    .setHeader(AUTHORIZATION_HEADER, apiKey)
                    .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            throw new NewsFeedClientException("Could not retrieve news feed.", e);
        }
    }

    private NewsFeedResponse parseResponse(String responseBody) throws NewsFeedClientException {
        try {
            NewsFeedResponse response = GSON.fromJson(responseBody, NewsFeedResponse.class);
            if (response == null) {
                throw new NewsFeedClientException("Parsed response is null.");
            }
            return response;
        } catch (Exception e) {
            throw new NewsFeedClientException("Error parsing the response JSON: " + e.getMessage(), e);
        }
    }

    private void validateResponse(NewsFeedResponse response) throws NewsFeedClientException {
        if (ERROR_STATUS.equals(response.status())) {
            String errorMessage = "Error code: " + response.code() + ", message: " + response.message();
            throw new NewsFeedClientException(errorMessage);
        }

        if (!OK_STATUS.equals(response.status())) {
            throw new NewsFeedClientException("Unexpected response from news feed service.");
        }
    }
}