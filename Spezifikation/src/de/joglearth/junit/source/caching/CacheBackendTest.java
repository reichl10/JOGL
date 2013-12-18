package de.joglearth.junit.source.caching;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.SourceResponseType;
import de.joglearth.source.caching.Cache;
import de.joglearth.source.caching.FileSystemCache;
import de.joglearth.source.caching.MemoryCache;
import de.joglearth.source.caching.PathTranslator;


public class CacheBackendTest {

    private byte[] requestSynchronous(Cache<String, byte[]> cache, String key) {
        class Listener implements SourceListener<String, byte[]> {

            public byte[] value;


            @Override
            public synchronized void requestCompleted(String key, byte[] value) {
                this.value = value;
                notify();
            }
        }
        try {
            Listener l = new Listener();
            byte[] result;
            synchronized (l) {
                SourceResponse<byte[]> response = cache.requestObject(key, l);
                result = response.value;

                if (response.response == SourceResponseType.ASYNCHRONOUS) {
                    l.wait();
                    result = l.value;
                }
            }
            return result;
        } catch (InterruptedException e) {
            return null;
        }
    }

    private void testBackend(Cache<String, byte[]> cache) {
        byte[] content = { 1, 2, 3, 4 }, content2 = { 7, 8, 9, 10 };
        cache.putObject("foo", content);
        Iterator<String> keys = cache.getExistingObjects().iterator();
        assertNotNull(keys);
        assertTrue(keys.hasNext());
        String key = keys.next();
        assertNotNull(key);
        assertEquals(key, "foo");
        assertFalse(keys.hasNext());
        cache.putObject("bar", content2);

        assertEquals(requestSynchronous(cache, "foo"), content);
        assertEquals(requestSynchronous(cache, "bar"), content2);
        assertNull(requestSynchronous(cache, "baz"));
        
        cache.dropAll();        
        assertNull(requestSynchronous(cache, "foo"));
        assertNull(requestSynchronous(cache, "bar"));
        assertNull(requestSynchronous(cache, "baz"));
    }

    @Test
    public void testMemoryCache() {
        testBackend(new MemoryCache<String, byte[]>());
    }


    private class TrivialPathTranslator implements PathTranslator<String> {

        @Override
        public String toFileSystemPath(String k) {
            return k;
        }

        @Override
        public String fromFileSystemPath(String s) {
            return s;
        }

    }


    @Test
    public void testFileSystemCache() {
        testBackend(new FileSystemCache<>(".", new TrivialPathTranslator()));
    }

}
