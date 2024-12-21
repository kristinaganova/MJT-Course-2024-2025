package src.bg.sofia.uni.fmi.mjt.goodreads.book;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BookTest {

    @Test
    void testValidBookCreation() {
        Book book = new Book("1", "Title", "Author", "Description",
                List.of("Genre1", "Genre2"), 4.5, 100, "http://example.com");

        assertNotNull(book, "Book should be created successfully when all parameters are valid");
    }

    @Test
    void testOfMethodValidInput() {
        String[] tokens = {"1", "Title", "Author", "Description",
                "['Genre1', 'Genre2']", "4.5", "100", "http://example.com"};

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
        String[] tokens = {"1", "Title", "Author", "Description",
                "['Genre1', 'Genre2']", "4.5", "100"}; // Липсва URL

        assertThrows(IllegalArgumentException.class, () -> Book.of(tokens),
                "Calling of method with incorrect token count should throw IllegalArgumentException");
    }

    @Test
    void testOfMethodWithInvalidNumberFormat() {
        String[] tokens = {"1", "Title", "Author", "Description",
                "['Genre1', 'Genre2']", "invalid", "100", "http://example.com"};

        assertThrows(IllegalArgumentException.class, () -> Book.of(tokens),
                "Calling of method with invalid rating format should throw IllegalArgumentException");
    }

    @Test
    void testOfMethodWithInvalidRatingCountFormat() {
        String[] tokens = {"1", "Title", "Author", "Description",
                "['Genre1', 'Genre2']", "4.5", "invalid", "http://example.com"};

        assertThrows(IllegalArgumentException.class, () -> Book.of(tokens),
                "Calling of method with invalid rating count format should throw IllegalArgumentException");
    }

}
