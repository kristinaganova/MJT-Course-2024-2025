package src.bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.descriptions;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.descriptions.TFIDF;
import bg.sofia.uni.fmi.mjt.goodreads.tokenizer.TextTokenizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class TFIDFTest {

    private Set<Book> books;
    private TextTokenizer tokenizer;
    private TFIDF tfidfCalculator;

    @BeforeEach
    void setUp() {
        Book book1 = new Book(
                "1",
                "Book 1",
                "Author 1",
                "This is a test description for book one.",
                List.of("Fiction", "Thriller"),
                4.5,
                1000,
                "http://book1.url"
        );

        Book book2 = new Book(
                "2",
                "Book 2",
                "Author 2",
                "This is another test description for book two.",
                List.of("Fiction", "Mystery"),
                4.0,
                850,
                "http://book2.url"
        );

        Book book3 = new Book(
                "3",
                "Book 3",
                "Author 3",
                "Completely different description for book three.",
                List.of("Non-Fiction"),
                3.5,
                200,
                "http://book3.url"
        );

        books = new HashSet<>();
        books.add(book1);
        books.add(book2);
        books.add(book3);

        tokenizer = new TextTokenizer(new StringReader("the\nis\nfor"));

        tfidfCalculator = new TFIDF(books, tokenizer);
    }

    @Test
    void testCalculateSimilarity() {
        Book book1 = books.iterator().next();
        Book book2 = books.stream().skip(1).findFirst().orElseThrow();

        double similarity = tfidfCalculator.calculateSimilarity(book1, book2);

        assertNotNull(similarity, "Similarity should not be null");
        assertTrue(similarity >= 0.0 && similarity <= 1.0, "Similarity should be between 0 and 1");
    }

    @Test
    void testCalculateSimilarityZero() {
        Book book1 = books.iterator().next();
        Book book3 = books.stream().skip(2).findFirst().orElseThrow();

        double similarity = tfidfCalculator.calculateSimilarity(book1, book3);

        assertNotNull(similarity, "Similarity should not be null");
        assertEquals(0.0, similarity, "Similarity should be 0 for completely different descriptions");
    }

    @Test
    void testComputeTF() {
        Book book1 = books.iterator().next();
        Map<String, Double> tf = tfidfCalculator.computeTF(book1);

        assertNotNull(tf, "TF map should not be null");
        assertTrue(tf.containsKey("test"), "TF map should contain 'test'");
        assertTrue(tf.containsKey("book"), "TF map should contain 'book'");
    }

    @Test
    void testComputeIDF() {
        Book book1 = books.iterator().next();
        Map<String, Double> idf = tfidfCalculator.computeIDF(book1);

        assertNotNull(idf, "IDF map should not be null");
        assertTrue(idf.containsKey("test"), "IDF map should contain 'test'");
        assertTrue(idf.containsKey("book"), "IDF map should contain 'book'");
    }
}