package bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.genres;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.SimilarityCalculator;

import java.util.HashSet;
import java.util.Set;

public class GenresOverlapSimilarityCalculator implements SimilarityCalculator {

    @Override
    public double calculateSimilarity(Book first, Book second) {
        Set<String> genresFirst = new HashSet<>(first.genres());
        Set<String> genresSecond = new HashSet<>(second.genres());

        Set<String> intersection = new HashSet<>(genresFirst);
        intersection.retainAll(genresSecond);

        int minSize = Math.min(first.genres().size(), second.genres().size());

        return minSize == 0 ? 0.0 : (double) intersection.size() / minSize;
    }
    
}
