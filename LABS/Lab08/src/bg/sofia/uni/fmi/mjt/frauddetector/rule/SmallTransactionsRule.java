package bg.sofia.uni.fmi.mjt.frauddetector.rule;

import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;

import java.util.List;

public class SmallTransactionsRule implements Rule {
    private final int countThreshold;
    private final double amountThreshold;
    private final double weight;

    public SmallTransactionsRule(int countThreshold, double amountThreshold, double weight) {
        this.countThreshold = countThreshold;
        this.amountThreshold = amountThreshold;
        this.weight = weight;
    }

    @Override
    public boolean applicable(List<Transaction> transactions) {
        long smallTransactionsCount = transactions.stream()
                .filter(t -> t.transactionAmount() < amountThreshold)
                .count();
        return smallTransactionsCount >= countThreshold;
    }

    @Override
    public double weight() {
        return weight;
    }
}
