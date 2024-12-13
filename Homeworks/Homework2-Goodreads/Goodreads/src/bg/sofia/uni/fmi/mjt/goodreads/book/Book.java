package bg.sofia.uni.fmi.mjt.goodreads.book;

import java.util.List;

public record Book(
        String ID,
        String title,
        String author,
        String description,
        List<String> genres,
        double rating,
        int ratingCount,
        String URL
) {
    private static final int MAX_RATING = 5;
    public Book {
        if (ID == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (title == null || title.isEmpty() || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        if (author == null || author.isEmpty() || author.isBlank()) {
            throw new IllegalArgumentException("Author cannot be null or empty");
        }
        if (description == null || description.isEmpty() || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        if (genres == null || genres.isEmpty()) {
            throw new IllegalArgumentException("Genres cannot be null or empty");
        }
        if (rating < 0 || rating > MAX_RATING) {
            throw new IllegalArgumentException("Rating should be between 0 and 5");
        }
        if (ratingCount < 0) {
            throw new IllegalArgumentException("Rating count cannot be negative");
        }
        if (URL == null || URL.isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }

    }

    private static final int EXPECTED_TOKEN_COUNT = 8;
    private static final int ID_INDEX = 0;
    private static final int TITLE_INDEX = 1;
    private static final int AUTHOR_INDEX = 2;
    private static final int DESCRIPTION_INDEX = 3;
    private static final int GENRES_INDEX = 4;
    private static final int RATING_INDEX = 5;
    private static final int RATING_COUNT_INDEX = 6;
    private static final int URL_INDEX = 7;
    private static final String GENRE_DELIMITER = ",";

    public static Book of(String[] tokens) {
        if (tokens == null || tokens.length != EXPECTED_TOKEN_COUNT) {
            throw new IllegalArgumentException("Invalid number of tokens, expected " + EXPECTED_TOKEN_COUNT);
        }

        List<String> genres = List.of(tokens[GENRES_INDEX].split(GENRE_DELIMITER));

        try {
            double rating = Double.parseDouble(tokens[RATING_INDEX]);
            int ratingCount = Integer.parseInt(tokens[RATING_COUNT_INDEX]);

            return new Book(
                    tokens[ID_INDEX],
                    tokens[TITLE_INDEX],
                    tokens[AUTHOR_INDEX],
                    tokens[DESCRIPTION_INDEX],
                    genres,
                    rating,
                    ratingCount,
                    tokens[URL_INDEX]
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format for rating or rating count", e);
        }
    }
}
