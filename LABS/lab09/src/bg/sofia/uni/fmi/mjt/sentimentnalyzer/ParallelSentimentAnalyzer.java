package bg.sofia.uni.fmi.mjt.sentimentnalyzer.src.bg.sofia.uni.fmi.mjt.sentimentnalyzer;

import bg.sofia.uni.fmi.mjt.sentimentnalyzer.exceptions.SentimentAnalysisException;
import bg.sofia.uni.fmi.mjt.sentimentnalyzer.threads.Consumer;
import bg.sofia.uni.fmi.mjt.sentimentnalyzer.threads.Producer;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class ParallelSentimentAnalyzer implements SentimentAnalyzerAPI {

    private final int workersCount;
    private final Set<String> stopWords;
    private final Map<String, SentimentScore> sentimentLexicon;
    private final Queue<AnalyzerInput> inputQueue;
    private final Map<String, SentimentScore> results;
    private final AtomicBoolean allInputsLoaded;

    private void validate(int workersCount, Set<String> stopWords, Map<String, SentimentScore> sentimentLexicon) {
        if (workersCount < 1) {
            throw new IllegalArgumentException("Number of worker threads must be greater than 0");
        }
        if (stopWords == null || stopWords.isEmpty()) {
            throw new IllegalArgumentException("Stop words cannot be null or empty");
        }
        if (sentimentLexicon == null || sentimentLexicon.isEmpty()) {
            throw new IllegalArgumentException("Sentiment lexicon cannot be null or empty");
        }
    }

    public ParallelSentimentAnalyzer(int workersCount, Set<String> stopWords, Map<String, SentimentScore> sentimentLexicon) {
        validate(workersCount, stopWords, sentimentLexicon);
        this.workersCount = workersCount;
        this.stopWords = stopWords;
        this.sentimentLexicon = sentimentLexicon;
        this.inputQueue = new ArrayDeque<>();
        this.results = new HashMap<>();
        this.allInputsLoaded = new AtomicBoolean(false);
    }

    private void validateInput(AnalyzerInput... input) {
        Set<String> uniqueIDs = new HashSet<>();
        for (AnalyzerInput analyzerInput : input) {
            if (analyzerInput != null) {
                if (!uniqueIDs.add(analyzerInput.inputID())) {
                    throw new IllegalArgumentException("Duplicate input ID detected: " + analyzerInput.inputID());
                }
            } else {
                throw new IllegalArgumentException("Input ID is null!");
            }
        }
    }

    private List<Thread> startProducers(AnalyzerInput... input) {
        List<Thread> producers = new ArrayList<>();
        for (AnalyzerInput analyzerInput : input) {
            Thread producer = new Thread(new Producer(analyzerInput, inputQueue));
            producers.add(producer);
            producer.start();
        }
        return producers;
    }

    private List<Thread> startConsumers(AnalyzerInput... input) {
        List<Thread> consumers = new ArrayList<>();
        for (int i = 0; i < workersCount; i++) {
            Thread consumer = new Thread(new Consumer(inputQueue, results, stopWords,
                                                      sentimentLexicon, allInputsLoaded));
            consumer.setName("Consumer-" + i);
            consumers.add(consumer);
            consumer.start();
        }
        return consumers;
    }

    @Override
    public Map<String, SentimentScore> analyze(AnalyzerInput... input) {
        validateInput(input);
        addInputsToQueue(input);

        List<Thread> producers = startProducers();
        List<Thread> consumers = startConsumers();

        //waitForThreads(producers, "Producer"); not sure if we should wait
        allInputsLoaded.set(true);
        waitForThreads(consumers, "Consumer");

        return results;
    }

    private void addInputsToQueue(AnalyzerInput... input) {
        for (AnalyzerInput analyzerInput : input) {
            synchronized (inputQueue) {
                inputQueue.offer(analyzerInput);
                inputQueue.notifyAll();
            }
        }
    }

    private void waitForThreads(List<Thread> threads, String threadType) {
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new SentimentAnalysisException(threadType + " thread interrupted", e);
            }
        }
    }
}