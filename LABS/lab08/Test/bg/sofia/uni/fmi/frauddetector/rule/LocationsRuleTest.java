package bg.sofia.uni.fmi.frauddetector.rule;

import bg.sofia.uni.fmi.frauddetector.transaction.Channel;
import bg.sofia.uni.fmi.frauddetector.transaction.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LocationsRuleTest {

    private List<Transaction> transactions;

    @BeforeEach
    public void setUp() {
        transactions = List.of(
                new Transaction("1", "acc1", 100.0, LocalDateTime.now(), "LocationA", Channel.ONLINE),
                new Transaction("2", "acc1", 105.0, LocalDateTime.now().minusHours(1), "LocationB", Channel.ONLINE),
                new Transaction("3", "acc1", 110.0, LocalDateTime.now().minusMinutes(30), "LocationC", Channel.ATM),
                new Transaction("4", "acc2", 500.0, LocalDateTime.now().minusDays(1), "LocationD", Channel.BRANCH)
        );
    }

    @Test
    public void testApplicableWithSufficientLocations() {
        LocationsRule rule = new LocationsRule(2, 0.3);
        assertTrue(rule.applicable(transactions));
    }

    @Test
    public void testNotApplicableWithFewLocations() {
        LocationsRule rule = new LocationsRule(100, 0.3);
        assertFalse(rule.applicable(transactions));
    }
}