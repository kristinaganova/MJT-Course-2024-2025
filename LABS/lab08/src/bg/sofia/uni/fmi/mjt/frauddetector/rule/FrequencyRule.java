package bg.sofia.uni.fmi.mjt.frauddetector.rule;

import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;

import java.time.temporal.TemporalAmount;
import java.util.List;
import java.util.stream.Collectors;

public class FrequencyRule implements Rule {

    private final int transactionCountThreshold;
    private final TemporalAmount timeWindow;
    private final double weight;

    public FrequencyRule(int transactionCountThreshold, TemporalAmount timeWindow, double weight) {
        this.transactionCountThreshold = transactionCountThreshold;
        this.timeWindow = timeWindow;
        this.weight = weight;
    }

    @Override
    public boolean applicable(List<Transaction> transactions) {
        return transactions.stream()
                .collect(Collectors.groupingBy(t ->t.transactionDate().toLocalDate(), Collectors.counting()))
                .values().stream().anyMatch(count->count >= transactionCountThreshold);
    }

    @Override
    public double weight() {
        return weight;
    }
}
