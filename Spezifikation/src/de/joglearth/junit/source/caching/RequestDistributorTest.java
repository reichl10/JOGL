package de.joglearth.junit.source.caching;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.SourceResponseType;
import de.joglearth.source.caching.MemoryCache;
import de.joglearth.source.caching.RequestDistributor;
import de.joglearth.source.caching.UnityMeasure;


public class RequestDistributorTest {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();


    private class TrivialSource implements Source<Object, Object> {

        private SourceResponseType t = SourceResponseType.MISSING;
        private Object r = new Object();
        private int requestCount = 0;


        @Override
        public synchronized SourceResponse<Object> requestObject(final Object key,
                final SourceListener<Object, Object> sender) {
            ++requestCount;
            switch (t) {
                case SYNCHRONOUS:
                    return new SourceResponse<>(t, r);
                case ASYNCHRONOUS:
                    executor.execute(new Runnable() {

                        @Override
                        public void run() {
                            synchronized (TrivialSource.this) {
                                sender.requestCompleted(key, r);
                            }
                        }
                    });
                default:
                    return new SourceResponse<>(t, null);
            }
        }

        public synchronized void setResponse(SourceResponseType type, Object value) {
            t = type;
            r = value;
        }

        public synchronized int getRequestCount() {
            return requestCount;
        }
    }

    private class CountingMemoryCache<Key, Value> extends MemoryCache<Key, Value> {

        private int requestCount = 0;
        private int dropCount = 0;


        @Override
        public synchronized SourceResponse<Value> requestObject(Key key,
                SourceListener<Key, Value> sender) {
            ++requestCount;
            return super.requestObject(key, sender);
        }

        @Override
        public void dropObject(Key key) {
            ++dropCount;
            super.dropObject(key);
        }

        public synchronized int getRequestCount() {
            return requestCount;
        }

        public synchronized int getDropCount() {
            return dropCount;
        }
    }


    RequestDistributor<Object, Object> dist;
    TrivialSource source;
    CountingMemoryCache<Object, Object> cache;
    Object responseValue;


    @Before
    public void setUp() {
        dist = new RequestDistributor<>(new UnityMeasure<>());
        source = new TrivialSource();
        cache = new CountingMemoryCache<>();
        dist.setSource(source);
        dist.addCache(cache, 3);
    }

    private static int countObjects(Iterator<Object> it) {
        int count = 0;
        while (it.hasNext()) {
            it.next();
            ++count;
        }
        return count;
    }

    @Test
    public void testSourceResponse() throws InterruptedException {
        source.setResponse(SourceResponseType.MISSING, new Object());
        SourceResponse<Object> r = dist.requestObject(new Object(), null);
        assertEquals(r.response, SourceResponseType.MISSING);
        assertNull(r.value);

        source.setResponse(SourceResponseType.ASYNCHRONOUS, new Integer(10));
        r = dist.requestObject(new Integer(2),
                new SourceListener<Object, Object>() {

                    @Override
                    public void requestCompleted(Object key, Object value) {
                        synchronized (RequestDistributorTest.this) {
                            responseValue = value;
                            RequestDistributorTest.this.notify();
                        }
                    }
                });
        assertEquals(r.response, SourceResponseType.ASYNCHRONOUS);
        assertNull(r.value);

        synchronized (this) {
            wait();
        }

        assertEquals(responseValue, new Integer(10));

        source.setResponse(SourceResponseType.SYNCHRONOUS, new Integer(42));
        r = dist.requestObject("", null);
        assertEquals(r.response, SourceResponseType.SYNCHRONOUS);
        assertEquals(r.value, new Integer(42));
    }

    @Test
    public void testRetrieveMultipleTimes() {
        source.setResponse(SourceResponseType.MISSING, null);
        SourceResponse<Object> r = dist.requestObject(new Integer(1), null);
        assertNull(r.value);
        assertEquals(r.response, SourceResponseType.MISSING);
        assertEquals(source.getRequestCount(), 1);
        assertEquals(countObjects(cache.getExistingObjects().iterator()), 0);

        source.setResponse(SourceResponseType.SYNCHRONOUS, new Integer(42));

        for (int i = 2; i < 4; ++i) {
            r = dist.requestObject(new Integer(1), null);
            assertEquals(r.response, SourceResponseType.SYNCHRONOUS);
            assertNotNull(r.value);
            assertEquals(r.value, new Integer(42));
            assertEquals(source.getRequestCount(), 2);
            assertEquals(countObjects(cache.getExistingObjects().iterator()), 1);
        }

        for (int i = 4; i < 6; ++i) {
            source.setResponse(SourceResponseType.MISSING, null);
            r = dist.requestObject(new Integer(1), null);
            assertEquals(r.response, SourceResponseType.SYNCHRONOUS);
            assertNotNull(r.value);
            assertEquals(r.value, new Integer(42));
        }
        assertEquals(source.getRequestCount(), 2);
        assertEquals(countObjects(cache.getExistingObjects().iterator()), 1);

    }

    @Test
    public void testFillCache() {
        source.setResponse(SourceResponseType.SYNCHRONOUS, new Integer(42));
        SourceResponse<Object> r;

        for (int i = 0; i < 3; ++i) {
            r = dist.requestObject(new Integer(i), null);
            assertEquals(r.response, SourceResponseType.SYNCHRONOUS);
            assertEquals(r.value, new Integer(42));
        }

        assertEquals(countObjects(cache.getExistingObjects().iterator()), 3);
        assertEquals(source.getRequestCount(), 3);
        assertEquals(cache.getRequestCount(), 3);
        assertEquals(cache.getDropCount(), 0);

        r = dist.requestObject(new Integer(3), null);
        assertEquals(r.response, SourceResponseType.SYNCHRONOUS);
        assertEquals(r.value, new Integer(42));
        assertTrue(countObjects(cache.getExistingObjects().iterator()) < 4);
        assertEquals(source.getRequestCount(), 4);
        assertTrue(cache.getDropCount() >= 1);
    }

    @Test
    public void testDropAll() {
        source.setResponse(SourceResponseType.SYNCHRONOUS, new Integer(42));
        for (int i = 0; i < 3; ++i) {
            SourceResponse<Object> r = dist.requestObject(new Integer(i), null);
        }
        assertEquals(countObjects(cache.getExistingObjects().iterator()), 3);
        dist.dropAll();
        assertEquals(countObjects(cache.getExistingObjects().iterator()), 0);
    }

}
