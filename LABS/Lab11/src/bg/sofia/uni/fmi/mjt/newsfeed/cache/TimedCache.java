package bg.sofia.uni.fmi.mjt.newsfeed.cache;

import java.util.HashMap;
import java.util.Map;

public class TimedCache<K, V> implements Cache<K, V> {

    private final long expirationTimeInSeconds;
    private final Map<K, CacheEntry<V>> cacheMap;

    private static class CacheEntry<V> {
        V value;
        long creationTime;

        CacheEntry(V value) {
            this.value = value;
            this.creationTime = System.currentTimeMillis();
        }
    }

    public TimedCache(long expirationTimeInSeconds) {
        this.expirationTimeInSeconds = expirationTimeInSeconds * 1000;
        this.cacheMap = new HashMap<>();
    }

    @Override
    public synchronized V get(K key) {
        CacheEntry<V> entry = cacheMap.get(key);
        if (entry == null) {
            return null;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - entry.creationTime > expirationTimeInSeconds) {
            cacheMap.remove(key);
            return null;
        }

        return entry.value;
    }

    @Override
    public synchronized void put(K key, V value) {
        cacheMap.put(key, new CacheEntry<>(value));
    }
}