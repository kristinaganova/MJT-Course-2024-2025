package bg.sofia.uni.fmi.mjt.goodreads.finder;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.tokenizer.TextTokenizer;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BookFinder implements BookFinderAPI {

    private final Set<Book> books;
    private final TextTokenizer tokenizer;

    public BookFinder(Set<Book> books, TextTokenizer tokenizer) {
        validateNonNullOrEmpty(books, "Books cannot be null or empty");
        validateNonNull(tokenizer, "Tokenizer cannot be null");
        this.books = books;
        this.tokenizer = tokenizer;
    }

    @Override
    public Set<Book> allBooks() {
        return Set.copyOf(books);
    }

    @Override
    public Set<String> allGenres() {
        return books.stream()
                .flatMap(book -> book.genres().stream())
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public List<Book> searchByAuthor(String authorName) {
        validateNonNullOrEmpty(authorName, "Author name cannot be null or empty");
        return search(book -> book.author().equalsIgnoreCase(authorName));
    }

    @Override
    public List<Book> searchByGenres(Set<String> genres, MatchOption option) {
        validateNonNullOrEmpty(genres, "Genres cannot be null or empty");
        validateNonNull(option, "Match option cannot be null");
        return search(book -> matchesGenres(book, genres, option));
    }

    private List<Book> search(SearchCriteria criteria) {
        return books.stream()
                .filter(criteria::matches)
                .toList();
    }

    private boolean matchesGenres(Book book, Set<String> genres, MatchOption option) {
        return option == MatchOption.MATCH_ALL
                ? book.genres().containsAll(genres)
                : genres.stream().anyMatch(book.genres()::contains);
    }

    @Override
    public List<Book> searchByKeywords(Set<String> keywords, MatchOption option) {
        validateNonNullOrEmpty(keywords, "Keywords cannot be null or empty");
        validateNonNull(option, "Match option cannot be null");

        Set<String> normalizedKeywords = keywords.stream()
                .filter(word -> !word.isBlank() && !tokenizer.stopwords().contains(word))
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        if (normalizedKeywords.isEmpty()) {
            return List.of();
        }

        return search(book -> matchesKeywords(book, normalizedKeywords, option));
    }

    private boolean matchesKeywords(Book book, Set<String> normalizedKeywords, MatchOption option) {
        Set<String> combinedTokens = combineTokens(
                tokenizer.tokenize(book.title()).stream().map(String::toLowerCase).collect(Collectors.toSet()),
                tokenizer.tokenize(book.description()).stream().map(String::toLowerCase).collect(Collectors.toSet())
        );

        if (combinedTokens.isEmpty()) {
            return false;
        }

        return option == MatchOption.MATCH_ALL
                ? normalizedKeywords.stream().allMatch(combinedTokens::contains)
                : normalizedKeywords.stream().anyMatch(combinedTokens::contains);
    }

    private Set<String> combineTokens(Set<String> tokens1, Set<String> tokens2) {
        Set<String> combined = new HashSet<>();
        if (tokens1 != null) combined.addAll(tokens1);
        if (tokens2 != null) combined.addAll(tokens2);
        return combined;
    }

    private void validateNonNullOrEmpty(Object input, String errorMessage) {
        if (input == null || (input instanceof Collection && ((Collection<?>) input).isEmpty()) ||
                (input instanceof String && ((String) input).isBlank())) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private void validateNonNull(Object input, String errorMessage) {
        if (input == null) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}