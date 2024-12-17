package src.bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.descriptions;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.descriptions.TFIDFSimilarityCalculator;
import bg.sofia.uni.fmi.mjt.goodreads.tokenizer.TextTokenizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TFIDFTest {

    private TextTokenizer tokenizer;
    private Set<Book> books;
    private TFIDFSimilarityCalculator tfidf;

    @BeforeEach
    void setUp() {
        tokenizer = mock(TextTokenizer.class);

        Book book1 = mock(Book.class);
        Book book2 = mock(Book.class);

        when(book1.description()).thenReturn("java programming");
        when(book2.description()).thenReturn("java coffee");

        when(tokenizer.tokenize("java programming"))
                .thenReturn(List.of("java", "programming"));
        when(tokenizer.tokenize("java coffee"))
                .thenReturn(List.of("java", "coffee"));

        books = new HashSet<>();
        books.add(book1);
        books.add(book2);

        tfidf = new TFIDFSimilarityCalculator(books, tokenizer);
    }

    @Test
    void testConstructorWithEmptyBooksShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new TFIDFSimilarityCalculator(new HashSet<>(), tokenizer));
    }

    @Test
    void testCalculateSimilarityWithEmptyDescriptionShouldReturnZero() {
        Book book1 = mock(Book.class);
        Book book2 = mock(Book.class);

        when(book1.description()).thenReturn("");
        when(book2.description()).thenReturn("Some valid description");
        when(tokenizer.tokenize("")).thenReturn(List.of()); // No words for the first book
        when(tokenizer.tokenize("Some valid description")).thenReturn(List.of("some", "valid", "description"));

        double similarity = tfidf.calculateSimilarity(book1, book2);

        assertEquals(0.0, similarity, "Expected similarity to be 0.0 when one book has no valid words");
    }

    @Test
    void testConstructorWithNullTokenizerShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new TFIDFSimilarityCalculator(books, null));
    }

    @Test
    void testCalculateSimilarityWithNullBooksShouldThrowException() {
        Book book1 = mock(Book.class);
        assertThrows(IllegalArgumentException.class, () -> tfidf.calculateSimilarity(book1, null));
        assertThrows(IllegalArgumentException.class, () -> tfidf.calculateSimilarity(null, book1));
    }

    @Test
    void testCalculateSimilarityShouldReturnCorrectValue() {
        Book book1 = mock(Book.class);
        Book book2 = mock(Book.class);

        when(book1.description()).thenReturn("java programming language");
        when(book2.description()).thenReturn("java coffee beans");

        when(tokenizer.tokenize("java programming language"))
                .thenReturn(List.of("java", "programming", "language"));
        when(tokenizer.tokenize("java coffee beans"))
                .thenReturn(List.of("java", "coffee", "beans"));

        double similarity = tfidf.calculateSimilarity(book1, book2);

        assertTrue(similarity >= 0.0 && similarity <= 1.0);
    }

    @Test
    void testComputeTFShouldReturnCorrectTFValues() {
        Book book = mock(Book.class);
        when(book.description()).thenReturn("java programming java");

        when(tokenizer.tokenize("java programming java"))
                .thenReturn(List.of("java", "programming", "java"));

        Map<String, Double> tf = tfidf.computeTF(book);

        assertEquals(2.0 / 3.0, tf.get("java"));
        assertEquals(1.0 / 3.0, tf.get("programming"));
    }

    @Test
    void testComputeIDFShouldReturnCorrectIDFValues() {
        Book book = books.stream().filter(b -> b.description().equals("java programming")).findFirst().orElseThrow();

        Map<String, Double> idf = tfidf.computeIDF(book);

        double expectedIdfJava = Math.log(2.0 / (2 + 1));
        double expectedIdfProgramming = Math.log(2.0 / (1 + 1));

        assertEquals(expectedIdfJava >= 0 ? expectedIdfJava: 0.0 , idf.get("java"), 1e-6, "IDF for 'java' is incorrect");
        assertEquals(expectedIdfProgramming, idf.get("programming"), 1e-6, "IDF for 'programming' is incorrect");
    }

    @Test
    void testComputeTFIDFShouldReturnNonEmptyMap() {
        Book book = mock(Book.class);
        when(book.description()).thenReturn("java programming");

        when(tokenizer.tokenize("java programming"))
                .thenReturn(List.of("java", "programming"));

        Map<String, Double> tfidfMap = tfidf.computeTFIDF(book);

        assertFalse(tfidfMap.isEmpty());
    }
}