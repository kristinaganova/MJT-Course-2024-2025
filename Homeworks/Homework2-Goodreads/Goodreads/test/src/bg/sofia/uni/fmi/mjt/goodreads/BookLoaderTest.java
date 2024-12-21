package src.bg.sofia.uni.fmi.mjt.goodreads;

import bg.sofia.uni.fmi.mjt.goodreads.BookLoader;
import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookLoaderTest {

    @Test
    void testLoadValidData() {
        String csvData = """
                N,Book,Author,Description,Genres,Avg_Rating,Num_Ratings,URL
                1,"Book One","Author A","Description one","[Fiction, Drama]",4.5,1000,"url1"
                2,"Book Two","Author B","Description two","[Mystery, Thriller]",4.2,800,"url2"
                """;

        var reader = new StringReader(csvData);
        Set<Book> books = BookLoader.load(reader);

        assertEquals(2, books.size(), "Expected two books to be loaded from valid CSV data");
    }

    @Test
    void testLoadEmptyData() {
        String csvData = "N,Book,Author,Description,Genres,Avg_Rating,Num_Ratings,URL\n";

        var reader = new StringReader(csvData);
        Set<Book> books = BookLoader.load(reader);

        assertTrue(books.isEmpty(), "Expected no books to be loaded from empty CSV data");
    }

    @Test
    void testLoadInvalidData() {
        String csvData = """
                N,Book,Author,Description,Genres,Avg_Rating,Num_Ratings,URL
                Invalid,Data,Not,Properly,Formatted
                """;

        var reader = new StringReader(csvData);

        assertThrows(IllegalArgumentException.class, () -> BookLoader.load(reader),
                "Expected IllegalArgumentException for invalid CSV data");
    }

    @Test
    void testLoadNullReader() {
        assertThrows(IllegalArgumentException.class, () -> BookLoader.load(null),
                "Expected IllegalArgumentException for null reader");
    }

    @Test
    void testLoadValidDataWithDuplicates() {
        String csvData = """
                N,Book,Author,Description,Genres,Avg_Rating,Num_Ratings,URL
                1,"Book One","Author A","Description one","[Fiction, Drama]",4.5,1000,"url1"
                1,"Book One","Author A","Description one","[Fiction, Drama]",4.5,1000,"url1"
                """;

        var reader = new StringReader(csvData);
        Set<Book> books = BookLoader.load(reader);

        assertEquals(1, books.size(), "Expected only one book to be loaded due to duplicate entries");
    }

}