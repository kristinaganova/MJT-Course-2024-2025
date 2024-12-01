package bg.sofia.uni.fmi.mjt.frauddetector.rule;

import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;

import java.util.List;

public class ZScoreRule implements Rule {

    private final double zScoreThreshold;
    private final double weight;

    public ZScoreRule(final double zScoreThreshold, final double weight) {
        this.zScoreThreshold = zScoreThreshold;
        this.weight = weight;
    }

    private double getMean(List<Transaction> transactions) {
        return transactions.stream().mapToDouble(Transaction::transactionAmount).average().orElse(0);
    }

    private double getVariance(List<Transaction> transactions, double mean) {
        return transactions.stream()
                .mapToDouble(Transaction::transactionAmount)
                .map(amount -> Math.pow(amount - mean, 2))
                .average()
                .orElse(0);
    }

    @Override
    public boolean applicable(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return false;
        }

        double mean = getMean(transactions);
        double variance = getVariance(transactions, mean);
        double standardDeviation = Math.sqrt(variance);

        if (standardDeviation == 0) {
            return false;
        }

        return transactions.stream()
                .mapToDouble(Transaction::transactionAmount)
                .anyMatch(amount -> {
                    double zScore = Math.abs(amount - mean) / standardDeviation;
                    return zScore > zScoreThreshold;
                });
    }

    @Override
    public double weight() {
        return weight;
    }
}
