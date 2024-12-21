package bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.descriptions;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.SimilarityCalculator;
import bg.sofia.uni.fmi.mjt.goodreads.tokenizer.TextTokenizer;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TFIDFSimilarityCalculator implements SimilarityCalculator {

    private final Set<Book> books;
    private final TextTokenizer tokenizer;

    private final Map<Book, Map<String, Double>> tfCache = new HashMap<>();
    private final Map<String, Double> idfCache = new HashMap<>();
    private final Map<Book, Set<String>> tokenizedDescriptionCache = new HashMap<>();

    public TFIDFSimilarityCalculator(Set<Book> books, TextTokenizer tokenizer) {
        if (books == null || books.isEmpty()) {
            throw new IllegalArgumentException("Books cannot be null or empty");
        }
        if (tokenizer == null) {
            throw new IllegalArgumentException("Tokenizer cannot be null");
        }
        this.books = books;
        this.tokenizer = tokenizer;

        cacheTokenizedDescriptions();
        precomputeIDF();
    }

    @Override
    public double calculateSimilarity(Book first, Book second) {
        validateBook(first);
        validateBook(second);

        Map<String, Double> tfidfFirst = computeTFIDF(first);
        Map<String, Double> tfidfSecond = computeTFIDF(second);

        return cosineSimilarity(tfidfFirst, tfidfSecond);
    }

    private void cacheTokenizedDescriptions() {
        for (Book book : books) {
            tokenizedDescriptionCache.put(book, new HashSet<>(tokenizer.tokenize(book.description())));
        }
    }

    private void precomputeIDF() {
        Map<String, Long> documentFrequency = books.stream()
                .flatMap(book -> tokenizedDescriptionCache.get(book).stream().distinct())
                .collect(Collectors.groupingBy(word -> word, Collectors.counting()));

        documentFrequency.forEach((word, docCount) -> {
            double idf = Math.log((double) books.size() / docCount);
            idfCache.put(word, Math.max(idf, 0.0)); // Avoid negative IDF values
        });
    }

    public Map<String, Double> computeTFIDF(Book book) {
        validateBook(book);

        Map<String, Double> tf = computeTF(book);

        if (tf.isEmpty()) {
            return Map.of();
        }

        Map<String, Double> tfidf = new HashMap<>();
        for (Map.Entry<String, Double> entry : tf.entrySet()) {
            String word = entry.getKey();
            double idf;
            if (idfCache.containsKey(word)) {
                idf = idfCache.get(word);
            } else {
                idf = computeIDFForWord(word);
                idfCache.put(word, idf);
            }
            tfidf.put(word, entry.getValue() * idf);
        }

        return tfidf;
    }

    private double computeIDFForWord(String word) {
        long documentCount = books.stream()
                .filter(book -> tokenizedDescriptionCache.get(book).contains(word))
                .count();

        double idf = Math.log((double) books.size() / (documentCount + 1));
        return Math.max(idf, 0.0);
    }

    public Map<String, Double> computeTF(Book book) {
        validateBook(book);

        return tfCache.computeIfAbsent(book, b -> {
            List<String> words = tokenizer.tokenize(b.description());
            if (words.isEmpty()) {
                return Map.of(); // Avoid division by zero for empty descriptions
            }

            long totalWords = words.size();
            Map<String, Long> wordCounts = words.stream()
                    .collect(Collectors.groupingBy(word -> word, Collectors.counting()));

            return wordCounts.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> entry.getValue() / (double) totalWords
                    ));
        });
    }

    public Map<String, Double> computeIDF(Book book) {
        validateBook(book);

        Set<String> words = tokenizedDescriptionCache.get(book);
        Map<String, Double> idfForBook = new HashMap<>();

        for (String word : words) {
            double idf = idfCache.computeIfAbsent(word, this::computeIDFForWord);
            idfForBook.put(word, idf);
        }

        return idfForBook;
    }

    private double cosineSimilarity(Map<String, Double> first, Map<String, Double> second) {
        double dotProduct = dotProduct(first, second);
        double magnitudeFirst = magnitude(first.values());
        double magnitudeSecond = magnitude(second.values());

        if (magnitudeFirst == 0 || magnitudeSecond == 0) {
            return 0.0;
        }

        return dotProduct / (magnitudeFirst * magnitudeSecond);
    }

    private double dotProduct(Map<String, Double> first, Map<String, Double> second) {
        return first.keySet().stream()
                .filter(second::containsKey)
                .mapToDouble(key -> first.get(key) * second.get(key))
                .sum();
    }

    private double magnitude(Collection<Double> input) {
        return Math.sqrt(input.stream()
                .mapToDouble(v -> v * v)
                .sum());
    }

    private void validateBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
    }
}
