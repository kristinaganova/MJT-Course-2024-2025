package bg.sofia.uni.fmi.mjt.newsfeed.client;

import bg.sofia.uni.fmi.mjt.newsfeed.exception.NewsFeedClientException;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.NewsFeedResponseException;
import bg.sofia.uni.fmi.mjt.newsfeed.model.datatransfer.NewsFeedArticles;
import bg.sofia.uni.fmi.mjt.newsfeed.model.datatransfer.NewsFeedResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;

public class NewsFeedHttpClient implements NewsFeedClient {

    private static final String CONFIG_FILE = "config.properties";
    private static final String API_KEY_PROPERTY = "api.key";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String OK_STATUS = "ok";
    private static final String ERROR_STATUS = "error";

    private final HttpClient httpClient;
    private final String apiKey;

    public NewsFeedHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
        this.apiKey = loadApiKey();

        if (this.apiKey == null || this.apiKey.isBlank()) {
            throw new IllegalStateException("API Key is not set in the config file.");
        }
    }

    public NewsFeedHttpClient(HttpClient httpClient, String apiKey) {
        this.httpClient = httpClient;
        this.apiKey = apiKey;
    }

    private String loadApiKey() {
        try (InputStream input =  getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IllegalStateException("Config file not found: " + CONFIG_FILE);
            }

            Properties properties = new Properties();
            properties.load(input);
            return properties.getProperty(API_KEY_PROPERTY);
        } catch (IOException e) {
            throw new IllegalStateException("Error reading the config file: " + CONFIG_FILE, e);
        }
    }

    @Override
    public NewsFeedArticles getNewsFeed(URI requestUri) throws NewsFeedResponseException, NewsFeedClientException {
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
            NewsFeedResponse response = new Gson().fromJson(responseBody, NewsFeedResponse.class);
            if (response == null) {
                throw new NewsFeedResponseException("Parsed response is null.");
            }
            return response;
        } catch (Exception e) {
            throw new NewsFeedClientException("Error parsing the response JSON: " + e.getMessage(), e);
        }
    }

    private void validateResponse(NewsFeedResponse response) throws NewsFeedResponseException {
        if (ERROR_STATUS.equals(response.status())) {
            String errorMessage = "Error code: " + response.code() + ", message: " + response.message();
            throw new NewsFeedResponseException(errorMessage);
        }

        if (!OK_STATUS.equals(response.status())) {
            throw new NewsFeedResponseException("Unexpected response from news feed service.");
        }
    }
}
