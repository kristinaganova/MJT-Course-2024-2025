package bg.sofia.uni.fmi.mjt.newsfeed.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.TimeUnit;

class TimedCacheTest {

    private TimedCache<String, String> cache;
    private static final long EXPIRATION_TIME = 2;

    @BeforeEach
    void setUp() {
        cache = new TimedCache<>(EXPIRATION_TIME);
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
        String key = "nonExistentKey";

        assertNull(cache.get(key), "Getting a non-existent key should return null.");
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
}
