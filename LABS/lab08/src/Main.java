import bg.sofia.uni.fmi.frauddetector.analyzer.TransactionAnalyzer;
import bg.sofia.uni.fmi.frauddetector.analyzer.TransactionAnalyzerImpl;
import bg.sofia.uni.fmi.frauddetector.rule.FrequencyRule;
import bg.sofia.uni.fmi.frauddetector.rule.LocationsRule;
import bg.sofia.uni.fmi.frauddetector.rule.Rule;
import bg.sofia.uni.fmi.frauddetector.rule.SmallTransactionsRule;
import bg.sofia.uni.fmi.frauddetector.rule.ZScoreRule;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.time.Period;
import java.util.List;

public class Main {

    public static void main(String... args) throws FileNotFoundException {
        String filePath = "C:\\Users\\user\\IdeaProjects\\lab08\\src\\resources\\dataset.csv";

        Reader reader = new FileReader(filePath);
        List<Rule> rules = List.of(
                new ZScoreRule(1.5, 0.3),
                new LocationsRule(3, 0.4),
                new FrequencyRule(4, Period.ofWeeks(4), 0.25),
                new SmallTransactionsRule(1, 10.20, 0.05)
        );

        TransactionAnalyzer analyzer = new TransactionAnalyzerImpl(reader, rules);

        System.out.println(analyzer.allAccountIDs());
        System.out.println(analyzer.allTransactionsByUser(analyzer.allTransactions().getFirst().accountID()));
        System.out.println(analyzer.accountsRisk());
    }

}