package bg.sofia.uni.fmi.mjt.frauddetector.rule;

import bg.sofia.uni.fmi.mjt.frauddetector.rule.FrequencyRule;
import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Channel;
import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FrequencyRuleTest {

    private List<Transaction> transactions;

    @BeforeEach
    public void setUp() {
        transactions = List.of(
                new Transaction("1", "acc1", 100.0, LocalDateTime.now(), "LocationA", Channel.ONLINE),
                new Transaction("2", "acc1", 200.0, LocalDateTime.now().minusHours(1), "LocationB", Channel.ONLINE),
                new Transaction("3", "acc1", 50.0, LocalDateTime.now().minusMinutes(30), "LocationC", Channel.ATM),
                new Transaction("4", "acc2", 500.0, LocalDateTime.now().minusDays(1), "LocationD", Channel.BRANCH)
        );
    }

    @Test
    public void testApplicableWithSufficientTransactions() {
        FrequencyRule rule = new FrequencyRule(3, Duration.ofDays(1), 0.5);
        assertTrue(rule.applicable(transactions), "Expected rule to be applicable with sufficient transactions.");
    }

    @Test
    public void testNotApplicableWithInsufficientTransactions() {
        FrequencyRule rule = new FrequencyRule(5, Duration.ofDays(1), 0.5);
        assertFalse(rule.applicable(transactions), "Expected rule to be not applicable with insufficient transactions.");
    }
}