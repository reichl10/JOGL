package de.joglearth.junit.caching;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.SourceResponseType;
import de.joglearth.source.caching.Cache;
import de.joglearth.source.caching.RequestDistributor;


public class RequestDistributorTest {

    @Test
    public final void testAddCache() {
        Cache<Integer, Integer> simpleCache = new Cache<Integer, Integer>() {

            Map<Integer, Integer> map = new HashMap<Integer, Integer>();


            @Override
            public SourceResponse<Integer> requestObject(Integer key,
                    SourceListener<Integer, Integer> sender) {
                return new SourceResponse<Integer>(SourceResponseType.SYNCHRONOUS, map.get(key));
            }

            @Override
            public void putObject(Integer k, Integer v) {
                map.put(k, v);
            }

            @Override
            public void dropObject(Integer k) {
                map.remove(k);
            }

            @Override
            public Iterable<Integer> getExistingObjects() {
                return map.values();
            }

            @Override
            public void dropAll() {
                map.clear();
            }

            @Override
            public void dispose() {
                // TODO Automatisch generierter Methodenstub
                
            }

        };
        RequestDistributor<Integer, Integer> t = new RequestDistributor<Integer, Integer>();
        try {
            t.addCache(null, 0);
        } catch (IllegalArgumentException e) {
            fail("This method does not accept null as cache argument but it's not in " +
            		"the documentation.");
        }
        t.addCache(simpleCache, 100);
    }

    @Test(timeout = 11000)
    public final void testRequestObject() {
        RequestDistributor<Integer, Integer> t = new RequestDistributor<Integer, Integer>();
        final Integer[] resultArray = new Integer[5];
        final Set<Integer> waitList = new HashSet<Integer>();
        Source<Integer, Integer> source = new TrivialTestSource();
        t.setSource(source);
        SourceListener<Integer, Integer> listener = new SourceListener<Integer, Integer>() {

            @Override
            public void requestCompleted(Integer key, Integer value) {
                if (key < 0 || key >= resultArray.length) {
                    fail("The RequestDistributor returned an invalid key!");
                }
                resultArray[key] = value;
                waitList.remove(key);
            }
        };
        for (int c = 0; c < resultArray.length; c++) {
            waitList.add(new Integer(c));
            SourceResponse<Integer> response = t.requestObject(new Integer(c), listener);
            assertNotNull("Response from requestObject was null", response);
            if (response.response == SourceResponseType.SYNCHRONOUS) {
                assertNotNull("Value of the synchronous response was null", response.value);
                resultArray[c] = response.value;
                waitList.remove(new Integer(c));
            }
        }

        while (waitList.size() > 0) {
            try {
                this.wait(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public final void testDropAll() {
        SourceListener<Integer, Integer> listener = new SourceListener<Integer, Integer>() {

            @Override
            public void requestCompleted(Integer key, Integer value) {}
        };
        class MockCache implements Cache<Integer, Integer> {

            Map<Integer, Integer> map = new HashMap<Integer, Integer>();


            @Override
            public SourceResponse<Integer> requestObject(Integer key,
                    SourceListener<Integer, Integer> sender) {
                return new SourceResponse<Integer>(SourceResponseType.SYNCHRONOUS, map.get(key));
            }

            @Override
            public void putObject(Integer k, Integer v) {
                map.put(k, v);
            }

            @Override
            public void dropObject(Integer k) {
                map.remove(k);
            }

            @Override
            public Iterable<Integer> getExistingObjects() {
                return map.values();
            }

            @Override
            public void dropAll() {
                map.clear();
            }

            public int getSizeOfCache() {
                return map.size();
            }

            @Override
            public void dispose() {
                // TODO Automatisch generierter Methodenstub
                
            }

        };
        
        MockCache simpleCache = new MockCache();
        RequestDistributor<Integer, Integer> t = new RequestDistributor<Integer, Integer>();
        t.addCache(simpleCache, 100);
        t.setSource(new TrivialTestSource());
        for (int i = 0; i < 100; i++) {
            t.requestObject(new Integer(i), listener);
        }
        assertEquals("The RequestDistributor did not store all objects.", new Integer(100),
                new Integer(simpleCache.getSizeOfCache()));
        t.dropAll();
        assertTrue("The RequestDistributor did not drop all objects.",
                0 == simpleCache.getSizeOfCache());
    }

    @Test
    public final void testDropAllPredicateOfKey() {
        fail("Not yet implemented"); // TODO
    }


    /**
     * Trivial Source
     */
    private class TrivialTestSource implements Source<Integer, Integer> {

        private SourceResponse<Integer> responseAsync = new SourceResponse<Integer>(
                                                              SourceResponseType.ASYNCHRONOUS, null);
        private ExecutorService         exec          = Executors.newFixedThreadPool(5);


        @Override
        public SourceResponse<Integer> requestObject(final Integer key,
                final SourceListener<Integer, Integer> sender) {
            int i = ThreadLocalRandom.current().nextInt(5);
            if (i == 4) {
                return new SourceResponse<Integer>(SourceResponseType.SYNCHRONOUS, 33);
            } else {
                Runnable a = new Runnable() {

                    @Override
                    public void run() {
                        try {
                            this.wait(ThreadLocalRandom.current().nextInt(1000));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        sender.requestCompleted(key, new Integer(34));
                    }
                };
                exec.execute(a);
                return responseAsync;
            }
        }


        @Override
        public void dispose() {
            // TODO Automatisch generierter Methodenstub
            
        }
    };
}
