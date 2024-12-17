package src.bg.sofia.uni.fmi.mjt.goodreads.tokenizer;

import bg.sofia.uni.fmi.mjt.goodreads.tokenizer.TextTokenizer;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TextTokenizerTest {

    @Test
    void testTokenizeValidInput() {
        var stopwordsReader = new StringReader("the\nis\nand");
        var tokenizer = new TextTokenizer(stopwordsReader);

        String input = "This is a simple test.";
        List<String> result = tokenizer.tokenize(input);

        assertEquals(List.of("this", "a", "simple", "test"), result,
                "Expected tokenized list without stopwords");
    }

    @Test
    void testTokenizeInputWithOnlyStopwords() {
        var stopwordsReader = new StringReader("the\nis\nand");
        var tokenizer = new TextTokenizer(stopwordsReader);

        String input = "the is and";
        List<String> result = tokenizer.tokenize(input);

        assertTrue(result.isEmpty(), "Expected no tokens when input contains only stopwords");
    }

    @Test
    void testTokenizeInputWithEmptyString() {
        var stopwordsReader = new StringReader("the\nis\nand");
        var tokenizer = new TextTokenizer(stopwordsReader);

        String input = "";
        List<String> result = tokenizer.tokenize(input);

        assertTrue(result.isEmpty(), "Expected empty list for empty input");
    }

    @Test
    void testTokenizeInputWithBlankString() {
        var stopwordsReader = new StringReader("the\nis\nand");
        var tokenizer = new TextTokenizer(stopwordsReader);

        String input = "    ";
        List<String> result = tokenizer.tokenize(input);

        assertTrue(result.isEmpty(), "Expected empty list for blank input");
    }

    @Test
    void testTokenizeNullInput() {
        var stopwordsReader = new StringReader("the\nis\nand");
        var tokenizer = new TextTokenizer(stopwordsReader);

        assertThrows(IllegalArgumentException.class, () -> tokenizer.tokenize(null),
                "Expected IllegalArgumentException for null input");
    }

    @Test
    void testStopwordsLoadedCorrectly() {
        var stopwordsReader = new StringReader("the\nis\nand");
        var tokenizer = new TextTokenizer(stopwordsReader);

        assertEquals(Set.of("the", "is", "and"), tokenizer.stopwords(),
                "Expected stopwords set to match input");
    }

    @Test
    void testTokenizeHandlesMixedCase() {
        var stopwordsReader = new StringReader("the\nis\nand");
        var tokenizer = new TextTokenizer(stopwordsReader);

        String input = "The quick brown fox.";
        List<String> result = tokenizer.tokenize(input);

        assertEquals(List.of("quick", "brown", "fox"), result,
                "Expected tokens to be case-insensitive");
    }

    @Test
    void testTokenizeHandlesPunctuation() {
        var stopwordsReader = new StringReader("the\nis\nand");
        var tokenizer = new TextTokenizer(stopwordsReader);

        String input = "Hello, world! This... is a test.";
        List<String> result = tokenizer.tokenize(input);

        assertEquals(List.of("hello", "world", "this", "a", "test"), result,
                "Expected punctuation to be ignored during tokenization");
    }

    @Test
    void testConstructorWithNullReader() {
        assertThrows(IllegalArgumentException.class, () -> new TextTokenizer(null),
                "Expected IllegalArgumentException when stopwordsReader is null");
    }

    @Test
    void testStopwordsWithEmptyReader() {
        var stopwordsReader = new StringReader("");
        var tokenizer = new TextTokenizer(stopwordsReader);

        assertTrue(tokenizer.stopwords().isEmpty(), "Expected no stopwords for empty reader input");
    }

    @Test
    void testStopwordsWithDuplicateEntries() {
        var stopwordsReader = new StringReader("the\nthe\nis\nand");
        var tokenizer = new TextTokenizer(stopwordsReader);

        assertEquals(Set.of("the", "is", "and"), tokenizer.stopwords(),
                "Expected stopwords set to handle duplicates correctly");
    }
}