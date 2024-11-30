package bg.sofia.uni.fmi.frauddetector.analyzer;

import bg.sofia.uni.fmi.frauddetector.rule.FrequencyRule;
import bg.sofia.uni.fmi.frauddetector.rule.LocationsRule;
import bg.sofia.uni.fmi.frauddetector.rule.Rule;
import bg.sofia.uni.fmi.frauddetector.rule.SmallTransactionsRule;
import bg.sofia.uni.fmi.frauddetector.transaction.Channel;
import bg.sofia.uni.fmi.frauddetector.transaction.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.time.Duration;
import java.util.List;
import java.util.SortedMap;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionAnalyzerImplTest {

    private TransactionAnalyzer analyzer;

    @BeforeEach
    public void setUp() {
        String mockCsvData = """
                TransactionID,AccountID,TransactionAmount,TransactionDate,Location,Channel
                1,acc1,100.0,2024-11-01 10:00:00,LocationA,ONLINE
                2,acc1,200.0,2024-11-01 12:00:00,LocationB,ATM
                3,acc2,50.0,2024-11-01 14:00:00,LocationA,BRANCH
                4,acc1,20.0,2024-11-02 10:00:00,LocationC,ONLINE
                5,acc2,300.0,2024-11-02 12:00:00,LocationD,BRANCH
                """;

        List<Rule> rules = List.of(
                new FrequencyRule(2, Duration.ofDays(1), 0.3),
                new LocationsRule(2, 0.4),
                new SmallTransactionsRule(2, 50.0, 0.3)
        );

        analyzer = new TransactionAnalyzerImpl(new StringReader(mockCsvData), rules);
    }

    @Test
    public void testAllTransactions() {
        List<Transaction> transactions = analyzer.allTransactions();
        assertEquals(5, transactions.size(), "Expected 5 transactions in the dataset.");
    }

    @Test
    public void testAllAccountIDs() {
        List<String> accountIDs = analyzer.allAccountIDs();
        assertEquals(2, accountIDs.size(), "Expected 2 unique account IDs.");
        assertTrue(accountIDs.contains("acc1"), "Expected 'acc1' to be in the list of account IDs.");
        assertTrue(accountIDs.contains("acc2"), "Expected 'acc2' to be in the list of account IDs.");
    }

    @Test
    public void testTransactionCountByChannel() {
        var transactionCountByChannel = analyzer.transactionCountByChannel();
        assertEquals(2, transactionCountByChannel.get(Channel.ONLINE), "Expected 2 ONLINE transactions.");
        assertEquals(1, transactionCountByChannel.get(Channel.ATM), "Expected 1 ATM transaction.");
        assertEquals(2, transactionCountByChannel.get(Channel.BRANCH), "Expected 2 BRANCH transactions.");
    }

    @Test
    public void testAmountSpentByUser() {
        double amountSpent = analyzer.amountSpentByUser("acc1");
        assertEquals(320.0, amountSpent, 0.001, "Expected amount spent by 'acc1' to be 320.0.");
    }

    @Test
    public void testAllTransactionsByUser() {
        List<Transaction> transactions = analyzer.allTransactionsByUser("acc1");
        assertEquals(3, transactions.size(), "Expected 3 transactions for 'acc1'.");
        assertTrue(transactions.stream().allMatch(t -> t.accountID().equals("acc1")),
                "All transactions should belong to 'acc1'.");
    }

    @Test
    public void testAccountRating() {
        double rating = analyzer.accountRating("acc1");
        assertEquals(0.7, rating, 0.001, "Expected risk rating for 'acc1' to be 0.7.");
    }

    @Test
    public void testAccountsRisk() {
        SortedMap<String, Double> risks = analyzer.accountsRisk();
        assertEquals(0.7, risks.get("acc1"), 0.001, "Expected risk for 'acc1' to be 0.7.");
        assertEquals(0.4, risks.get("acc2"), 0.001, "Expected risk for 'acc2' to be 0.4.");
    }

}
