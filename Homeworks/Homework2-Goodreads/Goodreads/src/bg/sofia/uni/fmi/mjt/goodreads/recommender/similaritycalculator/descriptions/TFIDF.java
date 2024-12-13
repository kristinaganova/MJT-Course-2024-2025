package bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.descriptions;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.SimilarityCalculator;
import bg.sofia.uni.fmi.mjt.goodreads.tokenizer.TextTokenizer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TFIDF implements SimilarityCalculator {

    private final Set<Book> books;
    private final TextTokenizer tokenizer;

    public TFIDF(Set<Book> books, TextTokenizer tokenizer) {
        validate(books, tokenizer);
        this.books = books;
        this.tokenizer = tokenizer;
    }

    void validate(Set<Book> books, TextTokenizer tokenizer) {
        if (books.isEmpty() || books == null) {
            throw new IllegalArgumentException("Books cannot be empty");
        }
        if (tokenizer == null) {
            throw new IllegalArgumentException("Tokenizer cannot be null");
        }
    }

    private void validateBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
    }

    @Override
    public double calculateSimilarity(Book first, Book second) {
        validateBook(first);
        validateBook(second);

        Map<String, Double> tfidfFirst = computeTFIDF(first);
        Map<String, Double> tfidfSecond = computeTFIDF(second);

        Set<String> allWords = getAllWords(tfidfFirst, tfidfSecond);

        double dotProduct = calculateDotProduct(allWords, tfidfFirst, tfidfSecond);
        double normFirst = calculateNorm(tfidfFirst);
        double normSecond = calculateNorm(tfidfSecond);

        if (normFirst == 0 || normSecond == 0) {
            return 0.0;
        }

        return dotProduct / (normFirst * normSecond);
    }

    private Set<String> getAllWords(Map<String, Double> tfidfFirst, Map<String, Double> tfidfSecond) {
        Set<String> allWords = new HashSet<>();
        allWords.addAll(tfidfFirst.keySet());
        allWords.addAll(tfidfSecond.keySet());
        return allWords;
    }

    private double calculateDotProduct(Set<String> allWords, Map<String, Double> tfidfFirst,
                                       Map<String, Double> tfidfSecond) {
        double dotProduct = 0.0;
        for (String word : allWords) {
            double tfid1 = tfidfFirst.getOrDefault(word, 0.0);
            double tfid2 = tfidfSecond.getOrDefault(word, 0.0);
            dotProduct += tfid1 * tfid2;
        }
        return dotProduct;
    }

    private double calculateNorm(Map<String, Double> tfidf) {
        double norm = 0.0;
        for (double tfid : tfidf.values()) {
            norm += tfid * tfid;
        }
        return Math.sqrt(norm);
    }

    public Map<String, Double> computeTFIDF(Book book) {
        validateBook(book);

        Map<String, Double> tf = computeTF(book);
        Map<String, Double> idf = computeIDF(book);

        Map<String, Double> tfidf = new HashMap<>();
        for (String word : tf.keySet()) {
            tfidf.put(word, tf.get(word) * idf.getOrDefault(word, 0.0));
        }

        return tfidf;
    }

    // #word/#allWords
    public Map<String, Double> computeTF(Book book) {
        validateBook(book);

        String descrption = book.description();
        Map<String, Long> wordsCount = tokenizer.tokenize(descrption).stream()
                .collect(Collectors.groupingBy(word -> word, Collectors.counting()));

        Map<String, Double> tf = new HashMap<>();
        long totalWords = descrption.split("\\W+").length;

        for (Map.Entry<String, Long> entry : wordsCount.entrySet()) {
            tf.put(entry.getKey(), (double) entry.getValue() / totalWords);
        }
        return tf;
    }

    //log( all books / descrWithWord + 1)
    public Map<String, Double> computeIDF(Book book) {
        validateBook(book);

        Set<String> allWords = tokenizer.tokenize(book.description()).stream().collect(Collectors.toSet());
        Map<String, Double> idfMap = new HashMap<>();

        for (String word : allWords) {
            long docCountWithWord = books.stream()
                    .filter(b -> tokenizer.tokenize(b.description()).contains(word))
                    .count();

            double idf = Math.log(books.size() / (double) (docCountWithWord + 1));
            idfMap.put(word, idf >= 0 ? idf : 0.0);
        }

        return idfMap;
    }

}
