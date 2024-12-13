package bg.sofia.uni.fmi.mjt.goodreads.recommender;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.SimilarityCalculator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;

public class BookRecommender implements BookRecommenderAPI {

    private final Set<Book> initialBooks;
    private final SimilarityCalculator similarityCalculator;

    public BookRecommender(Set<Book> initialBooks, SimilarityCalculator calculator) {
        validate(initialBooks, calculator);
        this.initialBooks = initialBooks;
        this.similarityCalculator = calculator;
    }

    void validate(Set<Book> books, SimilarityCalculator similarityCalculator) {
        if (books == null || books.isEmpty()) {
            throw new IllegalArgumentException("Similarity calculator cannot be null or empty");
        }
        if (similarityCalculator == null) {
            throw new IllegalArgumentException("Similarity calculator cannot be null");
        }
    }

    @Override
    public Map<Book, Double> recommendBooks(Book origin, int maxN) {
        validateInputs(origin, maxN);

        Map<Book, Double> bookSimilarities = calculateSimilarities(origin);

        return getTopNBooks(bookSimilarities, maxN);
    }

    private void validateInputs(Book origin, int maxN) {
        if (origin == null) {
            throw new IllegalArgumentException("Origin book cannot be null");
        }
        if (maxN <= 0) {
            throw new IllegalArgumentException("maxN must be greater than 0");
        }
    }

    private Map<Book, Double> calculateSimilarities(Book origin) {
        Map<Book, Double> similarities = new HashMap<>();
        for (Book book : initialBooks) {
            if (!book.equals(origin)) {
                double similarity = similarityCalculator.calculateSimilarity(origin, book);
                similarities.put(book, similarity);
            }
        }
        return similarities;
    }

    private Map<Book, Double> getTopNBooks(Map<Book, Double> bookSimilarities, int maxN) {
        return bookSimilarities.entrySet()
                .stream()
                .sorted((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()))
                .limit(maxN)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));
    }
    
}
