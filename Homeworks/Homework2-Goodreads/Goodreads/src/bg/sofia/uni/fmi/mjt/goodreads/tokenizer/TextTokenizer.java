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
                    .map(String::toLowerCase)
                    .map(String::strip)
                    .filter(word -> !word.isEmpty())
                    .collect(Collectors.toSet());
        } catch (IOException ex) {
            throw new IllegalArgumentException("Could not load stopwords dataset", ex);
        }
    }

    public List<String> tokenize(String input) {
        if (input == null || input.isBlank()) {
            return List.of();
        }

        String cleanedInput = input.replaceAll("\\p{Punct}", " ");

        cleanedInput = cleanedInput.toLowerCase().replaceAll("\\s+", " ").strip();

        String[] tokens = cleanedInput.split(" ");

        return Arrays.stream(tokens)
                .filter(word -> !word.isBlank() && !stopwords.contains(word))
                .toList();
    }

    public Set<String> stopwords() {
        return stopwords;
    }
}
