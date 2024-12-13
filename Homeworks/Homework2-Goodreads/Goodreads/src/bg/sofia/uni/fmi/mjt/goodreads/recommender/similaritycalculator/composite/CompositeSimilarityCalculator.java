package bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.composite;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.SimilarityCalculator;

import java.util.Map;

public class CompositeSimilarityCalculator implements SimilarityCalculator {

    private final Map<SimilarityCalculator, Double> similarityCalculatorMap;

    public CompositeSimilarityCalculator(Map<SimilarityCalculator, Double> similarityCalculatorMap) {
        if (similarityCalculatorMap == null || similarityCalculatorMap.isEmpty()) {
            throw new IllegalArgumentException("Similarity calculator map cannot be null or empty");
        }
        this.similarityCalculatorMap = similarityCalculatorMap;
    }

    @Override
    public double calculateSimilarity(Book first, Book second) {
        double weightedSum = 0.0;
        double totalWeight = 0.0;

        for (Map.Entry<SimilarityCalculator, Double> entry : similarityCalculatorMap.entrySet()) {
            SimilarityCalculator calculator = entry.getKey();
            double weight = entry.getValue();

            if (weight < 0) {
                throw new IllegalArgumentException("Weight cannot be negative");
            }

            double similarity = calculator.calculateSimilarity(first, second);
            weightedSum += similarity * weight;
            totalWeight += weight;
        }

        if (totalWeight == 0) {
            return 0.0;
        }

        return weightedSum / totalWeight;
    }
}