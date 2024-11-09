package bg.sofia.uni.fmi.mjt.eventbus.events;

import bg.sofia.uni.fmi.mjt.eventbus.events.Payload;

public class TextPayload implements Payload<String> {
    private final String text;

    public TextPayload(String text) {
        this.text = text;
    }

    @Override
    public int getSize() {
        return text.length();
    }

    @Override
    public String getPayload() {
        return text;
    }
}