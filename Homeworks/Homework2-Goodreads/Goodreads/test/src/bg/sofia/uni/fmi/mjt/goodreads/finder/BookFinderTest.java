package src.bg.sofia.uni.fmi.mjt.goodreads.finder;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.finder.BookFinder;
import bg.sofia.uni.fmi.mjt.goodreads.finder.MatchOption;
import bg.sofia.uni.fmi.mjt.goodreads.tokenizer.TextTokenizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookFinderTest {

    private Set<Book> books;
    private TextTokenizer tokenizer;
    private BookFinder bookFinder;

    @BeforeEach
    void setUp() {
        books = new HashSet<>();
        tokenizer = mock(TextTokenizer.class);
        bookFinder = new BookFinder(books, tokenizer);
    }

    @Test
    void testConstructorThrowsExceptionWhenBooksIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new BookFinder(null, tokenizer));
    }

    @Test
    void testConstructorThrowsExceptionWhenBooksIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new BookFinder(Collections.emptySet(), tokenizer));
    }

    @Test
    void testConstructorThrowsExceptionWhenTokenizerIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new BookFinder(books, null));
    }

    @Test
    void testAllBooksReturnsUnmodifiableSet() {
        Book book = mock(Book.class);
        books.add(book);

        Set<Book> result = bookFinder.allBooks();

        assertEquals(1, result.size());
        assertTrue(result.contains(book));
        assertThrows(UnsupportedOperationException.class, () -> result.add(mock(Book.class)));
    }

    @Test
    void testSearchThrowsExceptionWhenCriteriaIsNull() {
        assertThrows(IllegalArgumentException.class, () -> bookFinder.search(null));
    }

    @Test
    void testSearchByAuthorReturnsBooksByAuthor() {
        String author = "Author1";
        Book book1 = mock(Book.class);
        Book book2 = mock(Book.class);
        when(book1.author()).thenReturn(author);
        when(book2.author()).thenReturn("Author2");

        books.add(book1);
        books.add(book2);

        List<Book> result = bookFinder.searchByAuthor(author);

        assertEquals(1, result.size());
        assertTrue(result.contains(book1));
        assertFalse(result.contains(book2));
    }

    @Test
    void testSearchByAuthorThrowsExceptionWhenAuthorNameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> bookFinder.searchByAuthor(null));
    }

    @Test
    void testAllGenresReturnsAllGenres() {
        Book book1 = mock(Book.class);
        Book book2 = mock(Book.class);
        when(book1.genres()).thenReturn(List.of("Fiction", "Mystery"));
        when(book2.genres()).thenReturn(List.of("Fantasy", "Adventure"));

        books.add(book1);
        books.add(book2);

        Set<String> result = bookFinder.allGenres();

        assertEquals(4, result.size());
        assertTrue(result.contains("Fiction"));
        assertTrue(result.contains("Mystery"));
        assertTrue(result.contains("Fantasy"));
        assertTrue(result.contains("Adventure"));
    }

    @Test
    void testSearchByGenresWithMatchAll() {
        Set<String> genres = Set.of("Fiction", "Mystery");
        MatchOption option = MatchOption.MATCH_ALL;

        Book book1 = mock(Book.class);
        Book book2 = mock(Book.class);

        when(book1.genres()).thenReturn(List.of("Fiction", "Mystery"));
        when(book2.genres()).thenReturn(List.of("Fantasy", "Adventure"));

        books.add(book1);
        books.add(book2);

        List<Book> result = bookFinder.searchByGenres(genres, option);

        assertEquals(1, result.size());
        assertTrue(result.contains(book1));
        assertFalse(result.contains(book2));
    }

    @Test
    void testSearchByGenresWithMatchAny() {
        Set<String> genres = Set.of("Fiction", "Adventure");
        MatchOption option = MatchOption.MATCH_ANY;

        Book book1 = mock(Book.class);
        Book book2 = mock(Book.class);

        when(book1.genres()).thenReturn(List.of("Fiction", "Mystery"));
        when(book2.genres()).thenReturn(List.of("Fantasy", "Adventure"));

        books.add(book1);
        books.add(book2);

        List<Book> result = bookFinder.searchByGenres(genres, option);

        assertEquals(2, result.size());
        assertTrue(result.contains(book1));
        assertTrue(result.contains(book2));
    }

    @Test
    void testSearchByGenresThrowsExceptionWhenGenresIsNull() {
        assertThrows(IllegalArgumentException.class, () -> bookFinder.searchByGenres(null, MatchOption.MATCH_ALL));
    }

    @Test
    void testSearchByKeywordsWithMatchAll() {
        Set<String> keywords = Set.of("mystery", "detective");
        MatchOption option = MatchOption.MATCH_ALL;

        Book book = mock(Book.class);
        when(book.description()).thenReturn("A great mystery with a detective");

        books.add(book);

        when(tokenizer.tokenize(book.description())).thenReturn(List.of("a", "great", "mystery", "with", "a", "detective"));

        List<Book> result = bookFinder.searchByKeywords(keywords, option);

        assertEquals(1, result.size());
        assertTrue(result.contains(book));
    }

    @Test
    void testSearchByKeywordsWithMatchAny() {
        Set<String> keywords = Set.of("mystery", "adventure");
        MatchOption option = MatchOption.MATCH_ANY;

        Book book = mock(Book.class);
        when(book.description()).thenReturn("A great mystery with a detective");

        books.add(book);

        when(tokenizer.tokenize(book.description())).thenReturn(List.of("a", "great", "mystery", "with", "a", "detective"));

        List<Book> result = bookFinder.searchByKeywords(keywords, option);

        assertEquals(1, result.size());
        assertTrue(result.contains(book));
    }

    @Test
    void testSearchByKeywordsThrowsExceptionWhenKeywordsIsNull() {
        assertThrows(IllegalArgumentException.class, () -> bookFinder.searchByKeywords(null, MatchOption.MATCH_ALL));
    }
}