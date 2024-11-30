package bg.sofia.uni.fmi.frauddetector.analyzer;

import bg.sofia.uni.fmi.frauddetector.rule.Rule;
import bg.sofia.uni.fmi.frauddetector.transaction.Channel;
import bg.sofia.uni.fmi.frauddetector.transaction.Transaction;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class TransactionAnalyzerImpl implements TransactionAnalyzer {

    private final List<Transaction> transactions;
    private final List<Rule> rules;

    private static final double EPSILON = 1e-9;


    public TransactionAnalyzerImpl(Reader reader, List<Rule> rules) {
        if (Math.abs(rules.stream().mapToDouble(Rule::weight).sum() - 1.0) > EPSILON) {
            throw new IllegalArgumentException("Rules' weights must sum to 1.0");
        }

        this.rules = rules;

        try (BufferedReader br = new BufferedReader(reader)) {
            transactions = br.lines()
                    .skip(1) // Skip header
                    .map(Transaction::of)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error reading transactions", e);
        }
    }

    @Override
    public List<Transaction> allTransactions() {
        return new ArrayList<>(transactions);
    }

    @Override
    public List<String> allAccountIDs() {
        return transactions.stream()
                .map(Transaction::accountID)
                .distinct()
                .toList();
    }

    @Override
    public Map<Channel, Integer> transactionCountByChannel() {
        return transactions.stream()
                .collect(Collectors.groupingBy(Transaction::channel, Collectors.summingInt(t -> 1)));
    }

    @Override
    public double amountSpentByUser(String accountID) {
        if (accountID == null || accountID.isEmpty()) {
            throw new IllegalArgumentException("Account ID cannot be null or empty");
        }

        return transactions.stream()
                .filter(t -> t.accountID().equals(accountID))
                .mapToDouble(Transaction::transactionAmount)
                .sum();
    }

    @Override
    public List<Transaction> allTransactionsByUser(String accountId) {
        if (accountId == null || accountId.isEmpty()) {
            throw new IllegalArgumentException("Account ID cannot be null or empty");
        }

        return transactions.stream()
                .filter(t -> t.accountID().equals(accountId))
                .toList();
    }

    @Override
    public double accountRating(String accountId) {
        if (accountId == null || accountId.isEmpty()) {
            throw new IllegalArgumentException("Account ID cannot be null or empty");
        }

        List<Transaction> userTransactions = allTransactionsByUser(accountId);

        return rules.stream()
                .filter(rule -> rule.applicable(userTransactions))
                .mapToDouble(Rule::weight)
                .sum();
    }

    @Override
    public SortedMap<String, Double> accountsRisk() {
        SortedMap<String, Double> riskMap = new TreeMap<>(Comparator
                .comparingDouble((String accountId) -> accountRating(accountId)).reversed());
        allAccountIDs().forEach(accountId -> riskMap.put(accountId, accountRating(accountId)));
        return riskMap;
    }

}
