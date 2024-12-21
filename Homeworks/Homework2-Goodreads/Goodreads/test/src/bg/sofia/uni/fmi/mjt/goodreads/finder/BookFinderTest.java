package src.bg.sofia.uni.fmi.mjt.goodreads.finder;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.finder.BookFinder;
import bg.sofia.uni.fmi.mjt.goodreads.finder.MatchOption;
import bg.sofia.uni.fmi.mjt.goodreads.tokenizer.TextTokenizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookFinderTest {

    private BookFinder bookFinder;
    private TextTokenizer tokenizerMock;

    @BeforeEach
    void setUp() {
        tokenizerMock = mock(TextTokenizer.class);

        Set<Book> books = Set.of(
                new Book("1", "Book One", "Author A", "Description one",
                        List.of("Fiction", "Drama"), 4.5, 1000, "url1"),
                new Book("2", "Book Two", "Author B", "Description two",
                        List.of("Mystery", "Thriller"), 4.2, 800, "url2"),
                new Book("3", "Another Book", "Author A", "Another description",
                        List.of("Drama", "Romance"), 3.8, 500, "url3")
        );

        when(tokenizerMock.tokenize("Book One")).thenReturn(List.of("book", "one"));
        when(tokenizerMock.tokenize("Description one")).thenReturn(List.of("description", "one"));
        when(tokenizerMock.tokenize("Book Two")).thenReturn(List.of("book", "two"));
        when(tokenizerMock.tokenize("Description two")).thenReturn(List.of("description", "two"));
        when(tokenizerMock.tokenize("Another Book")).thenReturn(List.of("another", "book"));
        when(tokenizerMock.tokenize("Another description")).thenReturn(List.of("another", "description"));

        bookFinder = new BookFinder(books, tokenizerMock);
    }

    @Test
    void testAllBooks() {
        Set<Book> allBooks = bookFinder.allBooks();
        assertEquals(3, allBooks.size(), "The number of books should match the dataset size.");
    }

    @Test
    void testAllGenres() {
        Set<String> allGenres = bookFinder.allGenres();
        assertEquals(Set.of("Fiction", "Drama", "Mystery", "Thriller", "Romance"), allGenres,
                "The set of genres should include all unique genres from the dataset.");
    }

    @Test
    void testSearchByAuthorValid() {
        List<Book> booksByAuthorA = bookFinder.searchByAuthor("Author A");
        assertEquals(2, booksByAuthorA.size(), "Author A should have 2 books.");
    }

    @Test
    void testSearchByGenresMatchAll() {
        Set<String> genres = Set.of("Drama", "Fiction");

        when(tokenizerMock.tokenize(Mockito.anyString())).thenReturn(List.of("drama", "fiction"));

        List<Book> matchingBooks = bookFinder.searchByGenres(genres, MatchOption.MATCH_ALL);
        assertEquals(1, matchingBooks.size(), "There should be 1 book that matches all specified genres.");
    }

    @Test
    void testSearchByGenresMatchAny() {
        Set<String> genres = Set.of("Drama", "Mystery");

        when(tokenizerMock.tokenize(Mockito.anyString())).thenReturn(List.of("drama", "mystery"));

        List<Book> matchingBooks = bookFinder.searchByGenres(genres, MatchOption.MATCH_ANY);
        assertEquals(3, matchingBooks.size(), "There should be 3 books that match any of the specified genres.");
    }

    @Test
    void testSearchByKeywordsMatchAll() {
        Set<String> keywords = Set.of("description", "one");

        List<Book> matchingBooks = bookFinder.searchByKeywords(keywords, MatchOption.MATCH_ALL);

        assertEquals(1, matchingBooks.size(), "There should be 1 book matching all keywords.");
        assertEquals("Book One", matchingBooks.get(0).title(), "The matched book should be 'Book One'.");
    }

    @Test
    void testSearchByKeywordsMatchAny() {
        Set<String> keywords = Set.of("description", "Another");

        List<Book> matchingBooks = bookFinder.searchByKeywords(keywords, MatchOption.MATCH_ANY);

        assertEquals(3, matchingBooks.size(), "There should be 3 books matching any of the keywords.");
    }


    @Test
    void testSearchByKeywordsNull() {
        assertThrows(IllegalArgumentException.class, () -> bookFinder.searchByKeywords(null, MatchOption.MATCH_ALL),
                "Searching with null keywords should throw IllegalArgumentException.");
    }

    @Test
    void testSearchByKeywordsEmptyKeywords() {
        assertThrows(IllegalArgumentException.class, () -> bookFinder.searchByKeywords(Set.of(), MatchOption.MATCH_ALL),
                "Searching with empty keywords should throw IllegalArgumentException.");
    }

    @Test
    void testSearchByKeywordsNullOption() {
        Set<String> keywords = Set.of("description");
        assertThrows(IllegalArgumentException.class, () -> bookFinder.searchByKeywords(keywords, null),
                "Searching with a null match option should throw IllegalArgumentException.");
    }

    @Test
    void testConstructorWithNullBooks() {
        assertThrows(IllegalArgumentException.class, () -> new BookFinder(null, tokenizerMock),
                "Constructor with null books should throw IllegalArgumentException.");
    }

    @Test
    void testConstructorWithEmptyBooks() {
        assertThrows(IllegalArgumentException.class, () -> new BookFinder(Set.of(), tokenizerMock),
                "Constructor with empty books should throw IllegalArgumentException.");
    }

    @Test
    void testConstructorWithNullTokenizer() {
        Set<Book> books = Set.of(new Book("1", "Book One", "Author A", "Description one", List.of("Fiction"), 4.5, 1000, "url1"));
        assertThrows(IllegalArgumentException.class, () -> new BookFinder(books, null),
                "Constructor with null tokenizer should throw IllegalArgumentException.");
    }
}