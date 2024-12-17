package bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.genres;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.SimilarityCalculator;

import java.util.Set;

public class GenresOverlapSimilarityCalculator implements SimilarityCalculator {

    @Override
    public double calculateSimilarity(Book first, Book second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Books cannot be null");
        }
        if (first.genres() == null || second.genres() == null) {
            return 0.0;
        }

        Set<String> genresFirst = Set.copyOf(first.genres());
        Set<String> genresSecond = Set.copyOf(second.genres());

        if (genresFirst.isEmpty() || genresSecond.isEmpty()) {
            return 0.0;
        }

        long intersectionSize = genresFirst.stream()
                .filter(genresSecond::contains)
                .count();

        int minSize = Math.min(genresFirst.size(), genresSecond.size());
        return (double) intersectionSize / minSize;
    }
}
