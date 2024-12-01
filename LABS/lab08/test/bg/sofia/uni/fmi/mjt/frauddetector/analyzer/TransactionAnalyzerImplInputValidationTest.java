package bg.sofia.uni.fmi.mjt.frauddetector.analyzer;

import bg.sofia.uni.fmi.mjt.frauddetector.analyzer.TransactionAnalyzer;
import bg.sofia.uni.fmi.mjt.frauddetector.analyzer.TransactionAnalyzerImpl;
import bg.sofia.uni.fmi.mjt.frauddetector.rule.Rule;
import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TransactionAnalyzerImplInputValidationTest {

    private TransactionAnalyzer analyzer;

    @BeforeEach
    public void setUp() {
        String mockCsvData = """
                TransactionID,AccountID,TransactionAmount,TransactionDate,Location,Channel
                1,acc1,100.0,2024-11-01 10:00:00,LocationA,ONLINE
                """;

        List<Rule> mockRules = List.of(
                new Rule() {
                    @Override
                    public boolean applicable(List<Transaction> transactions) {
                        return true;
                    }

                    @Override
                    public double weight() {
                        return 0.5;
                    }
                },
                new Rule() {
                    @Override
                    public boolean applicable(List<Transaction> transactions) {
                        return false;
                    }

                    @Override
                    public double weight() {
                        return 0.5;
                    }
                }
        );

        analyzer = new TransactionAnalyzerImpl(new StringReader(mockCsvData), mockRules);
    }

    @Test
    public void testConstructorWithInvalidRulesSum() {
        List<Rule> invalidRules = List.of(
                new Rule() {
                    @Override
                    public boolean applicable(List<Transaction> transactions) {
                        return true;
                    }

                    @Override
                    public double weight() {
                        return 1;
                    }
                },
                new Rule() {
                    @Override
                    public boolean applicable(List<Transaction> transactions) {
                        return false;
                    }

                    @Override
                    public double weight() {
                        return 1;
                    }
                }
        );

        assertThrows(IllegalArgumentException.class, () ->
                        new TransactionAnalyzerImpl(new StringReader(""), invalidRules),
                "Expected IllegalArgumentException for rules' weights sum != 1.0");
    }

    @Test
    public void testAmountSpentByUserWithNullAccountID() {
        assertThrows(IllegalArgumentException.class, () ->
                        analyzer.amountSpentByUser(null),
                "Expected IllegalArgumentException for null account ID");
    }

    @Test
    public void testAmountSpentByUserWithEmptyAccountID() {
        assertThrows(IllegalArgumentException.class, () ->
                        analyzer.amountSpentByUser(""),
                "Expected IllegalArgumentException for empty account ID");
    }

    @Test
    public void testAllTransactionsByUserWithNullAccountID() {
        assertThrows(IllegalArgumentException.class, () ->
                        analyzer.allTransactionsByUser(null),
                "Expected IllegalArgumentException for null account ID");
    }

    @Test
    public void testAllTransactionsByUserWithEmptyAccountID() {
        assertThrows(IllegalArgumentException.class, () ->
                        analyzer.allTransactionsByUser(""),
                "Expected IllegalArgumentException for empty account ID");
    }

    @Test
    public void testAccountRatingWithNullAccountID() {
        assertThrows(IllegalArgumentException.class, () ->
                        analyzer.accountRating(null),
                "Expected IllegalArgumentException for null account ID");
    }

    @Test
    public void testAccountRatingWithEmptyAccountID() {
        assertThrows(IllegalArgumentException.class, () ->
                        analyzer.accountRating(""),
                "Expected IllegalArgumentException for empty account ID");
    }
}
