package bg.sofia.uni.fmi.mjt.eventbus.events;

import java.time.Instant;

public class SimpleEvent implements Event<TextPayload> {
    private final Instant timestamp;
    private final int priority;
    private final String source;
    private final TextPayload payload;

    public SimpleEvent(Instant timestamp, int priority, String source, TextPayload payload) {
        this.timestamp = timestamp;
        this.priority = priority;
        this.source = source;
        this.payload = payload;
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public TextPayload getPayload() {
        return payload;
    }
}