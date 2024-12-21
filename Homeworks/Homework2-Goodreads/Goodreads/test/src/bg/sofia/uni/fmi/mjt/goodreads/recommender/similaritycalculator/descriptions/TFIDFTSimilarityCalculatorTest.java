package src.bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.descriptions;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.descriptions.TFIDFSimilarityCalculator;
import bg.sofia.uni.fmi.mjt.goodreads.tokenizer.TextTokenizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TFIDFTSimilarityCalculatorTest {

    private TFIDFSimilarityCalculator calculator;
    private TextTokenizer tokenizer;

    private Book bookX;
    private Book bookY;
    private Book bookZ;

    @BeforeEach
    void setUp() {
        tokenizer = mock(TextTokenizer.class);

        bookX = mock(Book.class);
        when(bookX.description()).thenReturn("academy superhero club superhero");
        when(bookX.title()).thenReturn("Book X");

        bookY = mock(Book.class);
        when(bookY.description()).thenReturn("superhero mission save club");
        when(bookY.title()).thenReturn("Book Y");

        bookZ = mock(Book.class);
        when(bookZ.description()).thenReturn("crime murder mystery club");
        when(bookZ.title()).thenReturn("Book Z");

        when(tokenizer.tokenize("academy superhero club superhero"))
                .thenReturn(List.of("academy", "superhero", "club", "superhero"));
        when(tokenizer.tokenize("superhero mission save club"))
                .thenReturn(List.of("superhero", "mission", "save", "club"));
        when(tokenizer.tokenize("crime murder mystery club"))
                .thenReturn(List.of("crime", "murder", "mystery", "club"));
        when(tokenizer.tokenize("Book X")).thenReturn(List.of("book", "x"));
        when(tokenizer.tokenize("Book Y")).thenReturn(List.of("book", "y"));
        when(tokenizer.tokenize("Book Z")).thenReturn(List.of("book", "z"));

        calculator = new TFIDFSimilarityCalculator(Set.of(bookX, bookY, bookZ), tokenizer);
    }

    @Test
    void testComputeTF() {
        Map<String, Double> tf = calculator.computeTF(bookX);
        assertEquals(0.25, tf.get("academy"), 0.001);
        assertEquals(0.5, tf.get("superhero"), 0.001);
        assertEquals(0.25, tf.get("club"), 0.001);
    }

    @Test
    void testComputeIDF() {
        Map<String, Double> idf = calculator.computeIDF(bookX);
        assertEquals(Math.log(3.0 / 2), idf.get("superhero"), 0.001);
        assertEquals(Math.log(3.0 / 3), idf.get("club"), 0.001);
        assertEquals(Math.log(3.0 / 1), idf.get("academy"), 0.001);
    }

    @Test
    void testComputeTFIDF() {
        Map<String, Double> tfidf = calculator.computeTFIDF(bookX);
        assertEquals(0.25 * Math.log(3.0 / 1), tfidf.get("academy"), 0.001);
        assertEquals(0.5 * Math.log(3.0 / 2), tfidf.get("superhero"), 0.001);
        assertEquals(0.25 * Math.log(3.0 / 3), tfidf.get("club"), 0.001);
    }

    @Test
    void testCalculateSimilarity() {
        double similarity = calculator.calculateSimilarity(bookX, bookY);
        assertTrue(similarity >= 0.0 && similarity <= 1.0);
    }

    @Test
    void testEmptyBooksSetThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new TFIDFSimilarityCalculator(Set.of(), tokenizer));
    }

    @Test
    void testNullTokenizerThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new TFIDFSimilarityCalculator(Set.of(bookX, bookY, bookZ), null));
    }

    @Test
    void testEmptyDescription() {
        Book bookEmpty = mock(Book.class);
        when(bookEmpty.description()).thenReturn("");
        when(tokenizer.tokenize("")).thenReturn(List.of());

        Map<String, Double> tf = calculator.computeTF(bookEmpty);
        assertTrue(tf.isEmpty());

        Map<String, Double> tfidf = calculator.computeTFIDF(bookEmpty);
        assertTrue(tfidf.isEmpty());
    }

    @Test
    void testSingleWordDescription() {
        Book bookSingleWord = mock(Book.class);
        when(bookSingleWord.description()).thenReturn("unique");
        when(tokenizer.tokenize("unique")).thenReturn(List.of("unique"));

        Map<String, Double> tf = calculator.computeTF(bookSingleWord);
        assertEquals(1.0, tf.get("unique"), 0.001);

        Map<String, Double> tfidf = calculator.computeTFIDF(bookSingleWord);
        assertEquals(Math.log(3.0 / 1), tfidf.get("unique"), 0.001);
    }

    @Test
    void testNonOverlappingDescriptions() {
        Book bookA = mock(Book.class);
        Book bookB = mock(Book.class);
        when(bookA.description()).thenReturn("alpha beta gamma");
        when(bookB.description()).thenReturn("delta epsilon zeta");
        when(tokenizer.tokenize("alpha beta gamma")).thenReturn(List.of("alpha", "beta", "gamma"));
        when(tokenizer.tokenize("delta epsilon zeta")).thenReturn(List.of("delta", "epsilon", "zeta"));

        double similarity = calculator.calculateSimilarity(bookA, bookB);
        assertEquals(0.0, similarity, 0.001);
    }

    @Test
    void testIdenticalDescriptions() {
        Book bookA = mock(Book.class);
        when(bookA.description()).thenReturn("common words here");
        when(tokenizer.tokenize("common words here")).thenReturn(List.of("common", "words", "here"));

        double similarity = calculator.calculateSimilarity(bookA, bookA);
        assertEquals(1.0, similarity, 0.001);
    }

    @Test
    void testPartialOverlapDescriptions() {
        Book bookA = mock(Book.class);
        Book bookB = mock(Book.class);
        when(bookA.description()).thenReturn("shared words uniqueA");
        when(bookB.description()).thenReturn("shared words uniqueB");
        when(tokenizer.tokenize("shared words uniqueA")).thenReturn(List.of("shared", "words", "uniqueA"));
        when(tokenizer.tokenize("shared words uniqueB")).thenReturn(List.of("shared", "words", "uniqueB"));

        double similarity = calculator.calculateSimilarity(bookA, bookB);
        assertTrue(similarity > 0.0 && similarity < 1.0);
    }

    @Test
    void testDescriptionWithStopwordsOnly() {
        Book bookWithStopwords = mock(Book.class);
        when(bookWithStopwords.description()).thenReturn("the and or but");
        when(tokenizer.tokenize("the and or but")).thenReturn(List.of());

        Map<String, Double> tf = calculator.computeTF(bookWithStopwords);
        assertTrue(tf.isEmpty(), "TF should be empty when description contains only stopwords.");

        Map<String, Double> tfidf = calculator.computeTFIDF(bookWithStopwords);
        assertTrue(tfidf.isEmpty(), "TF-IDF should be empty when description contains only stopwords.");
    }

    @Test
    void testBookWithLargeDescription() {
        Book largeBook = mock(Book.class);
        String largeDescription = "word ".repeat(1000).trim();
        when(largeBook.description()).thenReturn(largeDescription);
        when(tokenizer.tokenize(largeDescription)).thenReturn(List.of("word").stream().limit(1000).toList());

        Map<String, Double> tf = calculator.computeTF(largeBook);
        assertEquals(1.0, tf.get("word"), 0.001, "TF should account for large descriptions correctly.");

        Map<String, Double> tfidf = calculator.computeTFIDF(largeBook);
        assertTrue(tfidf.containsKey("word"), "TF-IDF should handle large descriptions.");
    }

    @Test
    void testDescriptionWithSpecialCharacters() {
        Book bookWithSpecialChars = mock(Book.class);
        when(bookWithSpecialChars.description()).thenReturn("word1, word2! word3?");
        when(tokenizer.tokenize("word1, word2! word3?")).thenReturn(List.of("word1", "word2", "word3"));

        Map<String, Double> tf = calculator.computeTF(bookWithSpecialChars);
        assertEquals(1.0 / 3, tf.get("word1"), 0.001, "TF should ignore special characters.");
        assertEquals(1.0 / 3, tf.get("word2"), 0.001);
        assertEquals(1.0 / 3, tf.get("word3"), 0.001);
    }

    @Test
    void testBooksWithDifferentGenresAndTopics() {
        Book bookA = mock(Book.class);
        Book bookB = mock(Book.class);
        when(bookA.description()).thenReturn("mathematics calculus algebra");
        when(bookB.description()).thenReturn("history geography politics");
        when(tokenizer.tokenize("mathematics calculus algebra"))
                .thenReturn(List.of("mathematics", "calculus", "algebra"));
        when(tokenizer.tokenize("history geography politics"))
                .thenReturn(List.of("history", "geography", "politics"));

        double similarity = calculator.calculateSimilarity(bookA, bookB);
        assertEquals(0.0, similarity, 0.001, "Similarity should be 0 for completely unrelated topics.");
    }

    @Test
    void testBookWithRepeatedWords() {
        Book bookWithRepeats = mock(Book.class);
        when(bookWithRepeats.description()).thenReturn("repeat repeat repeat unique");
        when(tokenizer.tokenize("repeat repeat repeat unique"))
                .thenReturn(List.of("repeat", "repeat", "repeat", "unique"));

        Map<String, Double> tf = calculator.computeTF(bookWithRepeats);
        assertEquals(0.75, tf.get("repeat"), 0.001, "TF for 'repeat' should be calculated correctly.");
        assertEquals(0.25, tf.get("unique"), 0.001, "TF for 'unique' should be calculated correctly.");
    }

    @Test
    void testBooksWithSharedWordsDifferentFrequencies() {
        Book bookA = mock(Book.class);
        Book bookB = mock(Book.class);
        when(bookA.description()).thenReturn("common common uniqueA");
        when(bookB.description()).thenReturn("common uniqueB uniqueB");
        when(tokenizer.tokenize("common common uniqueA"))
                .thenReturn(List.of("common", "common", "uniqueA"));
        when(tokenizer.tokenize("common uniqueB uniqueB"))
                .thenReturn(List.of("common", "uniqueB", "uniqueB"));

        double similarity = calculator.calculateSimilarity(bookA, bookB);
        assertTrue(similarity > 0.0 && similarity < 1.0, "Similarity should reflect frequency differences.");
    }

    @Test
    void testTokenizerReturnsEmpty() {
        Book bookEmptyTokenized = mock(Book.class);
        when(bookEmptyTokenized.description()).thenReturn("### ### ###");
        when(tokenizer.tokenize("### ### ###")).thenReturn(List.of());

        Map<String, Double> tf = calculator.computeTF(bookEmptyTokenized);
        assertTrue(tf.isEmpty(), "TF should be empty for descriptions with no valid tokens.");

        Map<String, Double> tfidf = calculator.computeTFIDF(bookEmptyTokenized);
        assertTrue(tfidf.isEmpty(), "TF-IDF should be empty for descriptions with no valid tokens.");
    }

    @Test
    void testCaseInsensitiveTokenization() {
        Book bookMixedCase = mock(Book.class);
        when(bookMixedCase.description()).thenReturn("Word word WORD");
        when(tokenizer.tokenize("Word word WORD")).thenReturn(List.of("word", "word", "word"));

        Map<String, Double> tf = calculator.computeTF(bookMixedCase);
        assertEquals(1.0, tf.get("word"), 0.001, "TF should treat words with different cases as identical.");
    }
}
