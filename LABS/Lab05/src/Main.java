import bg.sofia.uni.fmi.mjt.eventbus.EventBus;
import bg.sofia.uni.fmi.mjt.eventbus.EventBusImpl;
import bg.sofia.uni.fmi.mjt.eventbus.events.SimpleEvent;
import bg.sofia.uni.fmi.mjt.eventbus.events.TextPayload;
import bg.sofia.uni.fmi.mjt.eventbus.subscribers.DeferredEventSubscriber;

import java.time.Instant;

public class Main {
    public static void main(String[] args) {

        EventBus eventBus = new EventBusImpl();

        DeferredEventSubscriber<SimpleEvent> deferredSubscriber = new DeferredEventSubscriber<>();
        eventBus.subscribe(SimpleEvent.class, deferredSubscriber);

        TextPayload payload = new TextPayload("Deferred Event Test");
        SimpleEvent deferredEvent = new SimpleEvent(Instant.now(), 1, "TestSource", payload);

        System.out.println("Publishing deferred event...");
        eventBus.publish(deferredEvent);

        System.out.println("Processing deferred events:");
        for (SimpleEvent d : deferredSubscriber) {
            System.out.println(d.getPayload().toString());
        }

    }
}
