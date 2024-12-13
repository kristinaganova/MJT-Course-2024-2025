package src.bg.sofia.uni.fmi.mjt.goodreads.recommender;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.BookRecommender;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.SimilarityCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookRecommenderTest {

    private BookRecommender bookRecommender;
    private SimilarityCalculator calculatorMock;
    private Set<Book> books;

    @BeforeEach
    void setUp() {
        books = Set.of(
                new Book("1", "Book One", "Author A", "Description one",
                        List.of("Fiction", "Drama"), 4.5, 1000, "url1"),
                new Book("2", "Book Two", "Author B", "Description two",
                        List.of("Mystery", "Thriller"), 4.2, 800, "url2"),
                new Book("3", "Another Book", "Author A", "Another description",
                        List.of("Drama", "Romance"), 3.8, 500, "url3")
        );

        calculatorMock = mock(SimilarityCalculator.class);

        bookRecommender = new BookRecommender(books, calculatorMock);
    }

    @Test
    void testRecommendBooksOriginNull() {
        assertThrows(IllegalArgumentException.class, () -> bookRecommender.recommendBooks(null, 2),
                "Should throw IllegalArgumentException when origin book is null");
    }

    @Test
    void testRecommendBooksMaxNInvalid() {
        Book origin = new Book("1", "Book One", "Author A", "Description one",
                List.of("Fiction", "Drama"), 4.5, 1000, "url1");

        assertThrows(IllegalArgumentException.class, () -> bookRecommender.recommendBooks(origin, 0),
                "Should throw IllegalArgumentException when maxN is <= 0");
    }

    @Test
    void testRecommendBooksValidInput() {
        Book origin = new Book("1", "Book One", "Author A", "Description one",
                List.of("Fiction", "Drama"), 4.5, 1000, "url1");

        when(calculatorMock.calculateSimilarity(eq(origin), any(Book.class))).thenReturn(0.5, 0.7);

        Map<Book, Double> recommendations = bookRecommender.recommendBooks(origin, 2);

        assertEquals(2, recommendations.size(), "Should recommend 2 books");
        assertTrue(recommendations.containsValue(0.7), "Recommendations should include the highest similarity score");
    }

    @Test
    void testRecommendBooksMaxNGreaterThanAvailableBooks() {
        Book origin = new Book("1", "Book One", "Author A", "Description one",
                List.of("Fiction", "Drama"), 4.5, 1000, "url1");

        when(calculatorMock.calculateSimilarity(eq(origin), any(Book.class))).thenReturn(0.5, 0.7);

        Map<Book, Double> recommendations = bookRecommender.recommendBooks(origin, 10);

        assertEquals(2, recommendations.size(), "Should recommend all available books if maxN exceeds book count");
    }

    @Test
    void testRecommendBooksExcludeOrigin() {
        Book origin = new Book("1", "Book One", "Author A", "Description one",
                List.of("Fiction", "Drama"), 4.5, 1000, "url1");

        when(calculatorMock.calculateSimilarity(eq(origin), any(Book.class))).thenReturn(0.5, 0.7);

        Map<Book, Double> recommendations = bookRecommender.recommendBooks(origin, 2);

        assertFalse(recommendations.containsKey(origin), "Origin book should not be in recommendations");
    }

}
