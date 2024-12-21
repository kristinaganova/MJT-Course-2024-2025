package src.bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.genres;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.genres.GenresOverlapSimilarityCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GenresOverlapSimilarityCalculatorTest {

    private GenresOverlapSimilarityCalculator calculator;
    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        calculator = new GenresOverlapSimilarityCalculator();

        book1 = mock(Book.class);
        book2 = mock(Book.class);
    }

    @Test
    void testCalculateSimilarityWithCompleteOverlap() {
        when(book1.genres()).thenReturn(List.of("Fantasy", "Adventure"));
        when(book2.genres()).thenReturn(List.of("Fantasy", "Adventure"));

        double similarity = calculator.calculateSimilarity(book1, book2);

        assertEquals(1.0, similarity, "Expected similarity to be 1.0 for complete overlap");
    }

    @Test
    void testCalculateSimilarityWithPartialOverlap() {
        when(book1.genres()).thenReturn(List.of("Fantasy", "Adventure", "Drama"));
        when(book2.genres()).thenReturn(List.of("Fantasy", "Adventure", "Sci-Fi"));

        double similarity = calculator.calculateSimilarity(book1, book2);

        double expected = 2.0 / 3.0; // Intersection: {"Fantasy", "Adventure"}, min size: 3
        assertEquals(expected, similarity, 1e-6, "Expected similarity to match partial overlap calculation");
    }

    @Test
    void testCalculateSimilarityWithNoOverlap() {
        when(book1.genres()).thenReturn(List.of("Fantasy", "Adventure"));
        when(book2.genres()).thenReturn(List.of("Sci-Fi", "Horror"));

        double similarity = calculator.calculateSimilarity(book1, book2);

        assertEquals(0.0, similarity, "Expected similarity to be 0.0 for no overlap");
    }

    @Test
    void testCalculateSimilarityWithEmptyGenresForBothBooks() {
        when(book1.genres()).thenReturn(List.of());
        when(book2.genres()).thenReturn(List.of());

        double similarity = calculator.calculateSimilarity(book1, book2);

        assertEquals(0.0, similarity, "Expected similarity to be 0.0 when both books have no genres");
    }

    @Test
    void testCalculateSimilarityWithEmptyGenresForOneBook() {
        when(book1.genres()).thenReturn(List.of("Fantasy", "Adventure"));
        when(book2.genres()).thenReturn(List.of());

        double similarity = calculator.calculateSimilarity(book1, book2);

        assertEquals(0.0, similarity, "Expected similarity to be 0.0 when one book has no genres");
    }

    @Test
    void testCalculateSimilarityWithDifferentGenreSetSizes() {
        when(book1.genres()).thenReturn(List.of("Fantasy", "Adventure"));
        when(book2.genres()).thenReturn(List.of("Fantasy"));

        double similarity = calculator.calculateSimilarity(book1, book2);

        double expected = 1.0 / 1.0; // Intersection: {"Fantasy"}, min size: 1
        assertEquals(expected, similarity, 1e-6, "Expected similarity to handle different genre set sizes correctly");
    }
}