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

    private static final int EXPECTED_TOKEN_COUNT = 8;
    private static final String GENRE_DELIMITER = ", ";

    private static final int ID_INDEX = 0;
    private static final int TITLE_INDEX = 1;
    private static final int AUTHOR_INDEX = 2;
    private static final int DESCRIPTION_INDEX = 3;
    private static final int GENRES_INDEX = 4;
    private static final int RATING_INDEX = 5;
    private static final int RATING_COUNT_INDEX = 6;
    private static final int URL_INDEX = 7;

    private static final String GENRES_BRACKET_LEFT = "[";
    private static final String GENRES_BRACKET_RIGHT = "]";
    private static final String GENRES_SINGLE_QUOTE = "'";

    public static Book of(String[] tokens) {
        if (tokens == null || tokens.length != EXPECTED_TOKEN_COUNT) {
            throw new IllegalArgumentException("Invalid number of tokens, expected " + EXPECTED_TOKEN_COUNT);
        }

        try {
            String id = tokens[ID_INDEX];
            String title = tokens[TITLE_INDEX];
            String author = tokens[AUTHOR_INDEX];
            String description = tokens[DESCRIPTION_INDEX];

            List<String> genres = parseGenres(tokens[GENRES_INDEX]);

            double rating = Double.parseDouble(tokens[RATING_INDEX]);
            int ratingCount = Integer.parseInt(tokens[RATING_COUNT_INDEX].replace(",", ""));
            String url = tokens[URL_INDEX];

            return new Book(id, title, author, description, genres, rating, ratingCount, url);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format for rating or rating count", e);
        }
    }

    private static List<String> parseGenres(String genresToken) {
        if (genresToken == null || genresToken.isBlank() || genresToken.equals("[]")) {
            return List.of();
        }

        return List.of(
                genresToken
                        .replace(GENRES_BRACKET_LEFT, "")
                        .replace(GENRES_BRACKET_RIGHT, "")
                        .replace(GENRES_SINGLE_QUOTE, "")
                        .split(GENRE_DELIMITER)
        );
    }
}