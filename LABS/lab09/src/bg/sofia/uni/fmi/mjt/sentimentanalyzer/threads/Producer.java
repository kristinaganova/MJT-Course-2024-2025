package bg.sofia.uni.fmi.mjt.sentimentanalyzer.threads;

import bg.sofia.uni.fmi.mjt.sentimentanalyzer.AnalyzerInput;

import java.util.Queue;

public class Producer implements Runnable {

    private final AnalyzerInput input;
    private final Queue<AnalyzerInput> queue;

    public Producer(AnalyzerInput input, Queue<AnalyzerInput> queue) {
        this.input = input;
        this.queue = queue;
    }

    @Override
    public void run() {
        synchronized (queue) {
            queue.offer(input);
            queue.notifyAll();
        }
    }
}