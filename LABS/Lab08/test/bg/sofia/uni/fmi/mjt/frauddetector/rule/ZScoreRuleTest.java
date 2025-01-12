package bg.sofia.uni.fmi.mjt.frauddetector.rule;

import bg.sofia.uni.fmi.mjt.frauddetector.rule.ZScoreRule;
import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Channel;
import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ZScoreRuleTest {

    private List<Transaction> transactions;

    @BeforeEach
    public void setUp() {
        transactions = List.of(
                new Transaction("1", "acc1", 100.0, LocalDateTime.now(), "LocationA", Channel.ONLINE),
                new Transaction("2", "acc1", 200.0, LocalDateTime.now().minusHours(1), "LocationB", Channel.ATM),
                new Transaction("3", "acc1", 50.0, LocalDateTime.now().minusMinutes(30), "LocationC", Channel.BRANCH)
        );
    }

    @Test
    public void testApplicableWithHighZScore() {
        ZScoreRule rule = new ZScoreRule(1.0, 0.5);

        assertTrue(rule.applicable(transactions), "Expected rule to be applicable with high Z-score.");
    }

    @Test
    public void testNotApplicableWithLowZScore() {
        ZScoreRule rule = new ZScoreRule(100.0, 0.6);
        assertFalse(rule.applicable(transactions));
    }

    @Test
    public void testApplicableWithNullTransactions() {
        ZScoreRule rule = new ZScoreRule(1.0, 0.5);
        assertFalse(rule.applicable(null), "Expected false for null transactions.");
    }

    @Test
    public void testApplicableWithEmptyTransactions() {
        ZScoreRule rule = new ZScoreRule(1.0, 0.5);
        assertFalse(rule.applicable(Collections.emptyList()), "Expected false for empty transaction list.");
    }

    @Test
    public void testApplicableWithZeroStandardDeviation() {
        ZScoreRule rule = new ZScoreRule(1.0, 0.5);

        List<Transaction> transactions = List.of(
                new Transaction("1", "acc1", 100.0, LocalDateTime.now(), "LocationA", Channel.ONLINE),
                new Transaction("2", "acc1", 100.0, LocalDateTime.now().minusHours(1), "LocationA", Channel.ONLINE),
                new Transaction("3", "acc1", 100.0, LocalDateTime.now().minusHours(2), "LocationA", Channel.ONLINE)
        );

        assertFalse(rule.applicable(transactions), "Expected false for transactions with zero standard deviation.");
    }

}