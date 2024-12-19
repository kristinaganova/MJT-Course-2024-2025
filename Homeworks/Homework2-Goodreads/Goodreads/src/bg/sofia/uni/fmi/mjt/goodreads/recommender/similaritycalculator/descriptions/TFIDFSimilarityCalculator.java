package bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.descriptions;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.SimilarityCalculator;
import bg.sofia.uni.fmi.mjt.goodreads.tokenizer.TextTokenizer;

import java.util.ArrayList;
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

    public TFIDFSimilarityCalculator(Set<Book> books, TextTokenizer tokenizer) {
        if (books == null || books.isEmpty()) {
            throw new IllegalArgumentException("Books cannot be null or empty");
        }
        if (tokenizer == null) {
            throw new IllegalArgumentException("Tokenizer cannot be null");
        }
        this.books = books;
        this.tokenizer = tokenizer;

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

    public Map<String, Double> computeTFIDF(Book book) {
        validateBook(book);

        Map<String, Double> tf = computeTF(book);
        Map<String, Double> tfidf = new HashMap<>();

        for (Map.Entry<String, Double> entry : tf.entrySet()) {
            String word = entry.getKey();
            double idf = computeIDF(book).get(word);
            tfidf.put(word, entry.getValue() * idf);
        }

        return tfidf;
    }

    private double computeIDFForWord(String word) {
        long documentCount = books.stream()
                .filter(book -> tokenizer.tokenize(book.description()).contains(word))
                .count();

        return Math.log((double) books.size() / (documentCount + 1));
    }

    public Map<String, Double> computeTF(Book book) {
        validateBook(book);

        if (tfCache.containsKey(book)) {
            return tfCache.get(book);
        }

        List<String> words = new ArrayList<>(tokenizer.tokenize(book.description()));

        long totalWords = words.size();

        if (totalWords == 0) {
            return new HashMap<>(); // Avoid division by zero for empty descriptions
        }

        Map<String, Long> wordCounts = words.stream()
                .collect(Collectors.groupingBy(word -> word, Collectors.counting()));

        Map<String, Double> tf = wordCounts.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue() / (double) totalWords
                ));

        tfCache.put(book, tf);
        return tf;
    }

    public Map<String, Double> computeIDF(Book book) {
        validateBook(book);
        Map<String, Double> idfForBook = new HashMap<>();

        Set<String> words = new HashSet<>(tokenizer.tokenize(book.description()));
        for (String word : words) {
            double idf = idfCache.containsKey(word) ? idfCache.get(word) : computeIDFForWord(word);
            idfForBook.put(word, idf);
        }

        return idfForBook;
    }

    private void precomputeIDF() {
        Map<String, Long> documentFrequency = new HashMap<>();

        for (Book book : books) {
            Set<String> uniqueWords = new HashSet<>(tokenizer.tokenize(book.description()));
            for (String word : uniqueWords) {
                documentFrequency.put(word, documentFrequency.getOrDefault(word, 0L) + 1);
            }
        }

        for (Map.Entry<String, Long> entry : documentFrequency.entrySet()) {
            String word = entry.getKey();
            long docCount = entry.getValue();
            double idf = Math.log((double) books.size() / docCount);
            idfCache.put(word, Math.max(idf, 0.0)); // Avoid negative IDF values
        }
    }

    private double cosineSimilarity(Map<String, Double> first, Map<String, Double> second) {
        Set<String> commonWords = new HashSet<>(first.keySet());
        commonWords.retainAll(second.keySet());

        double dotProduct = commonWords.stream()
                .mapToDouble(word -> first.get(word) * second.get(word))
                .sum();

        double magnitudeFirst = Math.sqrt(first.values().stream().mapToDouble(v -> v * v).sum());
        double magnitudeSecond = Math.sqrt(second.values().stream().mapToDouble(v -> v * v).sum());

        return (magnitudeFirst == 0 || magnitudeSecond == 0) ? 0.0 : dotProduct / (magnitudeFirst * magnitudeSecond);
    }

    private void validateBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
    }
}