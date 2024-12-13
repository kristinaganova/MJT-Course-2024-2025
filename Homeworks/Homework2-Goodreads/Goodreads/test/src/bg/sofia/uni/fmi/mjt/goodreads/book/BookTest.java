package src.bg.sofia.uni.fmi.mjt.goodreads.book;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    @Test
    void testValidBookCreation() {
        Book book = new Book("1", "Title", "Author", "Description", List.of("Genre1", "Genre2"), 4.5, 100, "http://example.com");
        assertNotNull(book, "Book should be created successfully when all parameters are valid");
    }

    @Test
    void testBookCreationWithNullId() {
        assertThrows(IllegalArgumentException.class, () ->
                        new Book(null, "Title", "Author", "Description", List.of("Genre1"), 4.5, 100, "http://example.com"),
                "Creating a book with null ID should throw IllegalArgumentException");
    }

    @Test
    void testBookCreationWithEmptyTitle() {
        assertThrows(IllegalArgumentException.class, () ->
                        new Book("1", "", "Author", "Description", List.of("Genre1"), 4.5, 100, "http://example.com"),
                "Creating a book with an empty title should throw IllegalArgumentException");
    }

    @Test
    void testBookCreationWithInvalidRating() {
        assertThrows(IllegalArgumentException.class, () ->
                        new Book("1", "Title", "Author", "Description", List.of("Genre1"), 6.0, 100, "http://example.com"),
                "Creating a book with a rating greater than 5 should throw IllegalArgumentException");
    }

    @Test
    void testBookCreationWithNegativeRatingCount() {
        assertThrows(IllegalArgumentException.class, () ->
                        new Book("1", "Title", "Author", "Description", List.of("Genre1"), 4.5, -10, "http://example.com"),
                "Creating a book with negative rating count should throw IllegalArgumentException");
    }

    @Test
    void testBookCreationWithNullGenres() {
        assertThrows(IllegalArgumentException.class, () ->
                        new Book("1", "Title", "Author", "Description", null, 4.5, 100, "http://example.com"),
                "Creating a book with null genres should throw IllegalArgumentException");
    }

    @Test
    void testBookCreationWithNullUrl() {
        assertThrows(IllegalArgumentException.class, () ->
                        new Book("1", "Title", "Author", "Description", List.of("Genre1"), 4.5, 100, null),
                "Creating a book with null URL should throw IllegalArgumentException");
    }

    @Test
    void testOfMethodValidInput() {
        String[] tokens = {"1", "Title", "Author", "Description", "Genre1,Genre2", "4.5", "100", "http://example.com"};
        Book book = Book.of(tokens);

        assertNotNull(book, "Book should be created successfully from valid tokens");
        assertEquals("1", book.ID(), "ID should match");
        assertEquals("Title", book.title(), "Title should match");
        assertEquals("Author", book.author(), "Author should match");
        assertEquals("Description", book.description(), "Description should match");
        assertEquals(List.of("Genre1", "Genre2"), book.genres(), "Genres should match");
        assertEquals(4.5, book.rating(), "Rating should match");
        assertEquals(100, book.ratingCount(), "Rating count should match");
        assertEquals("http://example.com", book.URL(), "URL should match");
    }

    @Test
    void testOfMethodWithNullTokens() {
        assertThrows(IllegalArgumentException.class, () -> Book.of(null),
                "Calling of method with null tokens should throw IllegalArgumentException");
    }

    @Test
    void testOfMethodWithInvalidTokenCount() {
        String[] tokens = {"1", "Title", "Author", "Description", "Genre1,Genre2", "4.5", "100"};
        assertThrows(IllegalArgumentException.class, () -> Book.of(tokens),
                "Calling of method with incorrect token count should throw IllegalArgumentException");
    }

    @Test
    void testOfMethodWithInvalidNumberFormat() {
        String[] tokens = {"1", "Title", "Author", "Description", "Genre1,Genre2", "invalid", "100", "http://example.com"};
        assertThrows(IllegalArgumentException.class, () -> Book.of(tokens),
                "Calling of method with invalid rating format should throw IllegalArgumentException");
    }
}
