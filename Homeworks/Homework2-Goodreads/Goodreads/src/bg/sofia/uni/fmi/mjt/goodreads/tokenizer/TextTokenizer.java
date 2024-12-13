package bg.sofia.uni.fmi.mjt.goodreads.tokenizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TextTokenizer {

    private final Set<String> stopwords;

    public TextTokenizer(Reader stopwordsReader) {
        if (stopwordsReader == null) {
            throw new IllegalArgumentException("Stopwords reader cannot be null");
        }
        try (var br = new BufferedReader(stopwordsReader)) {
            stopwords = br.lines()
                    .map(String::strip)
                    .filter(word -> !word.isEmpty())
                    .collect(Collectors.toSet());
        } catch (IOException ex) {
            throw new IllegalArgumentException("Could not load stopwords dataset", ex);
        }
    }

    public List<String> tokenize(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        if (input.isEmpty() || input.isBlank()) {
            throw new IllegalArgumentException("Input cannot be empty or blank");
        }

        input = input.toLowerCase();
        String[] words = input.split("\\W+");
        return Arrays.stream(words)
                .filter(word -> !stopwords.contains(word))
                .filter(word -> !word.isEmpty())
                .collect(Collectors.toList());
    }

    public Set<String> stopwords() {
        return stopwords;
    }
}