package src.bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.composite;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.SimilarityCalculator;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.composite.CompositeSimilarityCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompositeSimilarityCalculatorTest {

    private SimilarityCalculator calculator1;
    private SimilarityCalculator calculator2;
    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        calculator1 = mock(SimilarityCalculator.class);
        calculator2 = mock(SimilarityCalculator.class);

        book1 = mock(Book.class);
        book2 = mock(Book.class);
    }

    @Test
    void testConstructorWithNullMapShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new CompositeSimilarityCalculator(null));
    }

    @Test
    void testConstructorWithEmptyMapShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new CompositeSimilarityCalculator(Map.of()));
    }

    @Test
    void testCalculateSimilarityWithNegativeWeightShouldThrowException() {
        Map<SimilarityCalculator, Double> calculatorMap = Map.of(calculator1, -1.0);

        CompositeSimilarityCalculator composite = new CompositeSimilarityCalculator(calculatorMap);

        assertThrows(IllegalArgumentException.class, () -> composite.calculateSimilarity(book1, book2));
    }

    @Test
    void testCalculateSimilarityWithZeroWeightShouldReturnZero() {
        Map<SimilarityCalculator, Double> calculatorMap = Map.of(calculator1, 0.0);

        CompositeSimilarityCalculator composite = new CompositeSimilarityCalculator(calculatorMap);

        double result = composite.calculateSimilarity(book1, book2);

        assertEquals(0.0, result, "Expected similarity to be 0 when all weights are 0");
    }

    @Test
    void testCalculateSimilarityWithSingleCalculator() {
        when(calculator1.calculateSimilarity(book1, book2)).thenReturn(0.8);

        Map<SimilarityCalculator, Double> calculatorMap = Map.of(calculator1, 1.0);

        CompositeSimilarityCalculator composite = new CompositeSimilarityCalculator(calculatorMap);

        double result = composite.calculateSimilarity(book1, book2);

        assertEquals(0.8, result, 1e-6, "Expected similarity to match single calculator's similarity");
    }

    @Test
    void testCalculateSimilarityWithMultipleCalculators() {
        when(calculator1.calculateSimilarity(book1, book2)).thenReturn(0.7);
        when(calculator2.calculateSimilarity(book1, book2)).thenReturn(0.5);

        Map<SimilarityCalculator, Double> calculatorMap = Map.of(calculator1, 2.0, calculator2, 1.0);

        CompositeSimilarityCalculator composite = new CompositeSimilarityCalculator(calculatorMap);

        double result = composite.calculateSimilarity(book1, book2);

        double expected = (0.7 * 2.0 + 0.5 * 1.0) / (2.0 + 1.0);
        assertEquals(expected, result, 1e-6, "Expected similarity to be weighted average of individual similarities");
    }

    @Test
    void testCalculateSimilarityWithZeroTotalWeight() {
        when(calculator1.calculateSimilarity(book1, book2)).thenReturn(0.7);
        when(calculator2.calculateSimilarity(book1, book2)).thenReturn(0.5);

        Map<SimilarityCalculator, Double> calculatorMap = Map.of(calculator1, 0.0, calculator2, 0.0);

        CompositeSimilarityCalculator composite = new CompositeSimilarityCalculator(calculatorMap);

        double result = composite.calculateSimilarity(book1, book2);

        assertEquals(0.0, result, "Expected similarity to be 0 when total weight is 0");
    }
}