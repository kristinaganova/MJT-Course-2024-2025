package bg.sofia.uni.fmi.mjt.newsfeed.cache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TimedCache<K, V> implements Cache<K, V>, AutoCloseable {

    private final long expirationTimeInMillis;
    private final Map<K, CacheEntry<V>> cacheMap = new HashMap<>();
    private final Thread cleanupThread;
    private volatile boolean isRunning = true;

    private static class CacheEntry<V> {
        V value;
        long creationTime;

        CacheEntry(V value) {
            this.value = value;
            this.creationTime = System.currentTimeMillis();
        }
    }

    private static final int MULTIPLIER = 1000;

    public TimedCache(long expirationTimeInSeconds) {
        this.expirationTimeInMillis = expirationTimeInSeconds * MULTIPLIER;

        // Start the cleanup thread
        cleanupThread = new Thread(this::cleanupExpiredEntries);
        cleanupThread.setDaemon(true); // Ensure it does not block JVM shutdown
        cleanupThread.start();
    }

    @Override
    public synchronized V get(K key) {
        CacheEntry<V> entry = cacheMap.get(key);
        if (entry == null) {
            return null;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - entry.creationTime > expirationTimeInMillis) {
            cacheMap.remove(key);
            return null;
        }

        return entry.value;
    }

    @Override
    public synchronized void put(K key, V value) {
        cacheMap.put(key, new CacheEntry<>(value));
    }

    @Override
    public synchronized void remove(K key) {
        cacheMap.remove(key);
    }

    @Override
    public synchronized void clear() {
        cacheMap.clear();
    }

    private void cleanupExpiredEntries() {
        while (isRunning) {
            synchronized (this) {
                long currentTime = System.currentTimeMillis();
                Iterator<Map.Entry<K, CacheEntry<V>>> iterator = cacheMap.entrySet().iterator();

                while (iterator.hasNext()) {
                    Map.Entry<K, CacheEntry<V>> entry = iterator.next();
                    if (currentTime - entry.getValue().creationTime > expirationTimeInMillis) {
                        iterator.remove();
                    }
                }
            }

            try {
                Thread.sleep(expirationTimeInMillis / 2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    @Override
    public void close() {
        isRunning = false;
        cleanupThread.interrupt();
    }
}
