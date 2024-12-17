package bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.composite;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.SimilarityCalculator;

import java.util.Map;

public class CompositeSimilarityCalculator implements SimilarityCalculator {

    private final Map<SimilarityCalculator, Double> similarityCalculatorMap;

    public CompositeSimilarityCalculator(Map<SimilarityCalculator, Double> similarityCalculatorMap) {
        validateNonNullOrEmpty(similarityCalculatorMap);
        validateWeights(similarityCalculatorMap);
        this.similarityCalculatorMap = Map.copyOf(similarityCalculatorMap);
    }

    @Override
    public double calculateSimilarity(Book first, Book second) {
        validateBooks(first, second);

        double weightedSum = 0.0;
        double totalWeight = 0.0;

        for (Map.Entry<SimilarityCalculator, Double> entry : similarityCalculatorMap.entrySet()) {
            double weight = entry.getValue();
            double similarity = entry.getKey().calculateSimilarity(first, second);

            weightedSum += similarity * weight;
            totalWeight += weight;
        }

        return totalWeight == 0 ? 0.0 : weightedSum / totalWeight;
    }

    private void validateNonNullOrEmpty(Map<SimilarityCalculator, Double> map) {
        if (map == null || map.isEmpty()) {
            throw new IllegalArgumentException("Similarity calculator map cannot be null or empty");
        }
    }

    private void validateWeights(Map<SimilarityCalculator, Double> map) {
        if (map.values().stream().anyMatch(weight -> weight < 0)) {
            throw new IllegalArgumentException("Weights cannot be negative");
        }
    }

    private void validateBooks(Book first, Book second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Books cannot be null");
        }
    }
}