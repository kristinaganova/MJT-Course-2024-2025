package bg.sofia.uni.fmi.mjt.sentimentnalyzer.threads;

import bg.sofia.uni.fmi.mjt.sentimentnalyzer.AnalyzerInput;
import bg.sofia.uni.fmi.mjt.sentimentnalyzer.SentimentScore;
import bg.sofia.uni.fmi.mjt.sentimentnalyzer.exceptions.SentimentAnalysisException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class Consumer implements Runnable {

    private final Queue<AnalyzerInput> queue;
    private final Map<String, SentimentScore> results;
    private final Set<String> stopWords;
    private final Map<String, Integer> sentimentLexicon;
    private final AtomicBoolean allInputsLoaded;

    private static final int MAX_SENTIMENT_SCORE = 5;

    public Consumer(Queue<AnalyzerInput> queue, Map<String, SentimentScore> results, Set<String> stopWords,
                    Map<String, Integer> sentimentLexicon, AtomicBoolean allInputsLoaded) {
        this.queue = queue;
        this.results = results;
        this.stopWords = stopWords;
        this.sentimentLexicon = sentimentLexicon;
        this.allInputsLoaded = allInputsLoaded;
    }

    @Override
    public void run() {
        while (true) {
            AnalyzerInput input = getNextInput();
            if (input == null) {
                return;
            }

            processInput(input);
        }
    }

    private AnalyzerInput getNextInput() {
        synchronized (queue) {
            while (queue.isEmpty()) {
                if (allInputsLoaded.get()) {
                    return null;
                }
                try {
                    queue.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new SentimentAnalysisException("Consumer thread interrupted", e);
                }
            }
            return queue.poll();
        }
    }

    private void processInput(AnalyzerInput input) {
        try {
            String content = readContent(input.inputReader());
            int sentimentScore = analyzeText(content);
            SentimentScore sentiment = SentimentScore.fromScore(
                    Math.min(MAX_SENTIMENT_SCORE, Math.max(-MAX_SENTIMENT_SCORE, sentimentScore))
            );
            results.put(input.inputID(), sentiment);
        } catch (IOException e) {
            throw new SentimentAnalysisException("Error processing input: " + input.inputID(), e);
        }
    }

    private String readContent(Reader reader) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            content.append(line).append(" ");
        }
        return content.toString();
    }

    private String[] splitText(String text) {
        return text.toLowerCase()
                .replaceAll("[^a-zA-Z ]", " ")
                .split("\\s+");
    }

    private int analyzeText(String text) {
        String[] words = splitText(text);

        int score = 0;
        for (String word : words) {
            if (!stopWords.contains(word)) {
                score += sentimentLexicon.getOrDefault(word, 0);
            }
        }
        return score;
    }
}