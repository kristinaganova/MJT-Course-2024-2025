package bg.sofia.uni.fmi.mjt.goodreads.finder;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.tokenizer.TextTokenizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BookFinder implements BookFinderAPI {

    private final Set<Book> books;
    private final TextTokenizer tokenizer;

    public BookFinder(Set<Book> books, TextTokenizer tokenizer) {
        validate(books, tokenizer);
        this.books = books;
        this.tokenizer = tokenizer;
    }

    private void validate(Set<Book> books, TextTokenizer tokenizer) {
        if (books == null || books.isEmpty()) {
            throw new IllegalArgumentException("Books cannot be null or empty");
        }
        if (tokenizer == null) {
            throw new IllegalArgumentException("Tokenizer cannot be null");
        }
    }

    public Set<Book> allBooks() {
        return Collections.unmodifiableSet(books);
    }

    public List<Book> search(SearchCriteria criteria) {
        if (criteria == null) {
            throw new IllegalArgumentException("Search criteria cannot be null");
        }
        return books.stream()
                .filter(criteria::matches)
                .toList();
    }

    @Override
    public List<Book> searchByAuthor(String authorName) {
        if (authorName == null || authorName.isEmpty()) {
            throw new IllegalArgumentException("Author name cannot be null or empty");
        }
        return search(book -> book.author().equals(authorName));
    }

    @Override
    public Set<String> allGenres() {
        return books.stream()
                .flatMap(book -> book.genres().stream())
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public List<Book> searchByGenres(Set<String> genres, MatchOption option) {
        if (genres == null || genres.isEmpty()) {
            throw new IllegalArgumentException("Genres cannot be null or empty");
        }
        if (option == null) {
            throw new IllegalArgumentException("Match option cannot be null");
        }

        return search(book -> matchesGenres(book, genres, option));
    }

    @Override
    public List<Book> searchByKeywords(Set<String> keywords, MatchOption option) {
        if (keywords == null || keywords.isEmpty()) {
            throw new IllegalArgumentException("Keywords cannot be null or empty");
        }
        if (option == null) {
            throw new IllegalArgumentException("Match option cannot be null");
        }

        return search(book -> matchesKeywords(book, keywords, option));
    }

    private boolean matchesGenres(Book book, Set<String> genres, MatchOption option) {
        if (option == MatchOption.MATCH_ALL) {
            return book.genres().containsAll(genres);
        } else if (option == MatchOption.MATCH_ANY) {
            return genres.stream().anyMatch(book.genres()::contains);
        }
        return false;
    }

    private boolean matchesKeywords(Book book, Set<String> keywords, MatchOption option) {
        List<String> tokenizedTitle = tokenizer.tokenize(book.title());
        List<String> tokenizedDescription = tokenizer.tokenize(book.description());

        List<String> combinedTokens = combineTokens(tokenizedTitle, tokenizedDescription);

        if (option == MatchOption.MATCH_ALL) {
            return keywords.stream().allMatch(combinedTokens::contains);
        } else if (option == MatchOption.MATCH_ANY) {
            return keywords.stream().anyMatch(combinedTokens::contains);
        }

        return false;
    }

    private List<String> combineTokens(List<String> tokens1, List<String> tokens2) {
        if (tokens1 == null || tokens2 == null) {
            throw new IllegalArgumentException("Tokens cannot be null");
        }
        List<String> combinedTokens = new ArrayList<>();
        combinedTokens.addAll(tokens1);
        combinedTokens.addAll(tokens2);
        return combinedTokens;
    }
}