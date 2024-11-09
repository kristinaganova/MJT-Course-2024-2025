package bg.sofia.uni.fmi.mjt.eventbus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bg.sofia.uni.fmi.mjt.eventbus.events.Event;
import bg.sofia.uni.fmi.mjt.eventbus.exception.MissingSubscriptionException;
import bg.sofia.uni.fmi.mjt.eventbus.subscribers.Subscriber;

public class EventBusImpl implements EventBus {
    private final Map<Class<? extends Event<?>>, Set<Subscriber<?>>> subscribersMap = new HashMap<>();
    private final Map<Class<? extends Event<?>>, List<Event<?>>> eventLogs = new HashMap<>();

    @Override
    public <T extends Event<?>> void subscribe(Class<T> eventType, Subscriber<? super T> subscriber) {
        if (eventType == null || subscriber == null) {
            throw new IllegalArgumentException("Event type and subscriber cannot be null");
        }
        Set<Subscriber<?>> subscribers = subscribersMap.get(eventType);
        if (subscribers == null) {
            subscribers = new HashSet<>();
            subscribersMap.put(eventType, subscribers);
        }
        subscribers.add(subscriber);
    }

    @Override
    public <T extends Event<?>> void unsubscribe(Class<T> eventType, Subscriber<? super T> subscriber)
            throws MissingSubscriptionException {
        if (eventType == null || subscriber == null) {
            throw new IllegalArgumentException("Event type and subscriber cannot be null");
        }

        Set<Subscriber<?>> subscribers = subscribersMap.get(eventType);
        if (subscribers == null || !subscribers.remove(subscriber)) {
            throw new MissingSubscriptionException("Subscriber not found for the specified event type");
        }

        if (subscribers.isEmpty()) {
            subscribersMap.remove(eventType);
        }
    }

    @Override
    public <T extends Event<?>> void publish(T event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }

        Class<?> eventType = event.getClass();
        Set<Subscriber<?>> subscribers = subscribersMap.get(eventType);

        if (subscribers != null) {
            for (Subscriber<?> subscriber : subscribers) {
                Subscriber<T> specificSubscriber = (Subscriber<T>) subscriber;
                specificSubscriber.onEvent(event);
            }
        }

        List<Event<?>> eventsList = eventLogs.get(eventType);
        if (eventsList == null) {
            eventsList = new ArrayList<>();
            eventLogs.put((Class<? extends Event<?>>) eventType, eventsList);
        }
        eventsList.add(event);
    }

    @Override
    public void clear() {
        subscribersMap.clear();
        eventLogs.clear();
    }

    @Override
    public Collection<? extends Event<?>> getEventLogs(Class<? extends Event<?>> eventType, Instant from, Instant to) {

        if (eventType == null || from == null || to == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }

        List<Event<?>> events = eventLogs.getOrDefault(eventType, Collections.emptyList());

        List<Event<?>> filteredEvents = new ArrayList<>();
        for (Event<?> event : events) {
            if (!event.getTimestamp().isBefore(from) && event.getTimestamp().isBefore(to)) {
                filteredEvents.add(event);
            }
        }

        Collections.sort(filteredEvents, new Comparator<Event<?>>() {
            @Override
            public int compare(Event<?> e1, Event<?> e2) {
                return e1.getTimestamp().compareTo(e2.getTimestamp());
            }
        });

        return Collections.unmodifiableList(filteredEvents);
    }

    @Override
    public <T extends Event<?>> Collection<Subscriber<?>> getSubscribersForEvent(Class<T> eventType) {

        if (eventType == null) {
            throw new IllegalArgumentException("Event type cannot be null");
        }
        Set<Subscriber<?>> subscribers = subscribersMap.get(eventType);
        if (subscribers == null) {
            subscribers = Collections.emptySet();
        }
        return Collections.unmodifiableSet(subscribers);
    }
}
