package bg.sofia.uni.fmi.mjt.goodreads.recommender;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.SimilarityCalculator;

import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;

public class BookRecommender implements BookRecommenderAPI {

    private final Set<Book> initialBooks;
    private final SimilarityCalculator similarityCalculator;

    public BookRecommender(Set<Book> initialBooks, SimilarityCalculator calculator) {
        validateBooks(initialBooks);
        validateSimilarityCalculator(calculator);
        this.initialBooks = Set.copyOf(initialBooks);
        this.similarityCalculator = calculator;
    }

    @Override
    public SortedMap<Book, Double> recommendBooks(Book origin, int maxN) {
        validateInputs(origin, maxN);

        PriorityQueue<Map.Entry<Book, Double>> topBooks = findTopSimilarBooks(origin, maxN);

        return collectTopBooksInDescendingOrder(topBooks);
    }

    private PriorityQueue<Map.Entry<Book, Double>> findTopSimilarBooks(Book origin, int maxN) {
        PriorityQueue<Map.Entry<Book, Double>> topBooks = new PriorityQueue<>(Map.Entry.comparingByValue());

        for (Book book : initialBooks) {
            processBookSimilarity(origin, book, topBooks, maxN);
        }

        return topBooks;
    }

    private void processBookSimilarity(Book origin, Book book, PriorityQueue<Map.Entry<Book,
                                       Double>> topBooks, int maxN) {
        if (!book.equals(origin)) {
            double similarity = similarityCalculator.calculateSimilarity(origin, book);
            if (similarity > 0.0) {
                topBooks.offer(Map.entry(book, similarity));
                if (topBooks.size() > maxN) {
                    topBooks.poll();
                }
            }
        }
    }

    private SortedMap<Book, Double> collectTopBooksInDescendingOrder(PriorityQueue<Map.Entry<Book, Double>> topBooks) {
        Comparator<Book> customComparator = (book1, book2) -> {
            Double similarity1 = getSimilarityScore(topBooks, book1);
            Double similarity2 = getSimilarityScore(topBooks, book2);

            int result = similarity2.compareTo(similarity1);
            if (result == 0) {
                return book1.title().compareTo(book2.title());
            }
            return result;
        };

        Map<Book, Double> linkedMap = topBooks.stream()
                .sorted(Map.Entry.<Book, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));

        TreeMap<Book, Double> sortedMap = new TreeMap<>(customComparator);
        sortedMap.putAll(linkedMap);

        return sortedMap;
    }

    private Double getSimilarityScore(PriorityQueue<Map.Entry<Book, Double>> topBooks, Book book) {
        return topBooks.stream()
                .filter(entry -> entry.getKey().equals(book))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(0.0);
    }

    private static void validateBooks(Set<Book> books) {
        if (books == null || books.isEmpty()) {
            throw new IllegalArgumentException("Books cannot be null or empty");
        }
    }

    private static void validateSimilarityCalculator(SimilarityCalculator calculator) {
        if (calculator == null) {
            throw new IllegalArgumentException("Similarity calculator cannot be null");
        }
    }

    private static void validateInputs(Book origin, int maxN) {
        if (origin == null) {
            throw new IllegalArgumentException("Origin book cannot be null");
        }
        if (maxN <= 0) {
            throw new IllegalArgumentException("maxN must be greater than 0");
        }
    }
}