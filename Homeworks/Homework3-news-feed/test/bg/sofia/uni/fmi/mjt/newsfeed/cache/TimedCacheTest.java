package bg.sofia.uni.fmi.mjt.newsfeed.cache;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class TimedCacheTest {

    private TimedCache<String, String> cache;
    private static final long EXPIRATION_TIME = 2;

    @BeforeEach
    void setUp() {
        cache = new TimedCache<>(EXPIRATION_TIME);
    }

    @AfterEach
    void tearDown() {
        cache.close();
    }

    @Test
    void testPutAndGet() {
        String key = "key1";
        String value = "value1";

        cache.put(key, value);
        assertEquals(value, cache.get(key), "The value retrieved should match the value stored.");
    }

    @Test
    void testGetNonExistentKey() {
        assertNull(cache.get("nonExistentKey"), "Getting a non-existent key should return null.");
    }

    @Test
    void testExpiration() throws InterruptedException {
        String key = "key2";
        String value = "value2";

        cache.put(key, value);
        assertEquals(value, cache.get(key), "The value should be present immediately after insertion.");

        TimeUnit.SECONDS.sleep(EXPIRATION_TIME + 1);
        assertNull(cache.get(key), "The value should be null after expiration time.");
    }

    @Test
    void testOverwriteValue() {
        String key = "key3";
        String initialValue = "initialValue";
        String updatedValue = "updatedValue";

        cache.put(key, initialValue);
        assertEquals(initialValue, cache.get(key), "The initial value should be stored correctly.");

        cache.put(key, updatedValue);
        assertEquals(updatedValue, cache.get(key), "The updated value should overwrite the initial value.");
    }

    @Test
    void testMultipleKeys() {
        String key1 = "key1";
        String value1 = "value1";
        String key2 = "key2";
        String value2 = "value2";

        cache.put(key1, value1);
        cache.put(key2, value2);

        assertEquals(value1, cache.get(key1), "The value for key1 should be correct.");
        assertEquals(value2, cache.get(key2), "The value for key2 should be correct.");
    }

    @Test
    void testRemoveKey() {
        String key = "key4";
        String value = "value4";

        cache.put(key, value);
        cache.remove(key);
        assertNull(cache.get(key), "The value should be null after it has been removed.");
    }

    @Test
    void testClearCache() {
        cache.put("key1", "value1");
        cache.put("key2", "value2");

        cache.clear();
        assertNull(cache.get("key1"), "Cache should be cleared.");
        assertNull(cache.get("key2"), "Cache should be cleared.");
    }

    @Test
    void testAutoCloseable() {
        try (TimedCache<String, String> autoCloseableCache = new TimedCache<>(EXPIRATION_TIME)) {
            autoCloseableCache.put("key1", "value1");
            assertEquals("value1", autoCloseableCache.get("key1"), "The value should be retrievable within the AutoCloseable scope.");
        }
        // Verify the thread stops correctly
        assertDoesNotThrow(() -> cache.close(), "Closing the cache should not throw an exception.");
    }

    @Test
    void testConcurrentAccess() throws InterruptedException {
        int numberOfThreads = 10;
        Thread[] threads = new Thread[numberOfThreads];
        String key = "concurrentKey";
        String value = "concurrentValue";

        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(() -> cache.put(key, value));
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        assertEquals(value, cache.get(key), "The value should be consistent after concurrent writes.");
    }

    @Test
    void testCleanupThreadRemovesExpiredEntries() throws InterruptedException {
        String key1 = "key1";
        String value1 = "value1";

        cache.put(key1, value1);

        TimeUnit.SECONDS.sleep(EXPIRATION_TIME + 1);

        assertNull(cache.get(key1), "Expired entries should be removed by the cleanup thread.");
    }
}
