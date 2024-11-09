package bg.sofia.uni.fmi.mjt.eventbus.subscribers;

import bg.sofia.uni.fmi.mjt.eventbus.events.Event;

public class SimpleSubscriber<T extends Event<?>> implements Subscriber<T> {
    private final String name;

    public SimpleSubscriber(String name) {
        this.name = name;
    }

    @Override
    public void onEvent(T event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        System.out.println(name + " received event from source: " + event.getSource()
                                + " with priority: " + event.getPriority());
    }
}