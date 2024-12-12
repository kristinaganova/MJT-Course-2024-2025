package bg.sofia.uni.fmi.mjt.sentimentnalyzer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        try {
            Set<String> stopWords = new HashSet<>();
            try (BufferedReader reader = new BufferedReader(new FileReader("resources/stopwords.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    stopWords.add(line.trim());
                }
            }

            Map<String, Integer> sentimentLexicon = new HashMap<>();
            try (BufferedReader reader = new BufferedReader(new FileReader("resources/AFINN-111.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\t");
                    if (parts.length == 2) {
                        sentimentLexicon.put(parts[0], Integer.parseInt(parts[1]));
                    }
                }
            }

            ParallelSentimentAnalyzer analyzer = new ParallelSentimentAnalyzer(2, stopWords, sentimentLexicon);

            AnalyzerInput input1 = new AnalyzerInput("doc1", new StringReader("I love programming but I hate bugs."));
            AnalyzerInput input2 = new AnalyzerInput("doc2", new StringReader("The weather is beautiful and the day is great!"));
            AnalyzerInput input3 = new AnalyzerInput("doc3", new StringReader("Life is complicated and hard sometimes."));

            Map<String, SentimentScore> results = analyzer.analyze(input1, input2, input3);

            results.forEach((docId, sentimentScore) ->
                    System.out.println("Document: " + docId + " | Sentiment: " + sentimentScore));

        } catch (Exception e) {
            System.err.println("An error occurred during sentiment analysis: " + e.getMessage());
        }
    }
}
