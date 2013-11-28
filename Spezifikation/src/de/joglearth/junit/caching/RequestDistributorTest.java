package de.joglearth.junit.caching;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.joglearth.source.Source;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.SourceResponseType;
import de.joglearth.source.SourceListener;
import de.joglearth.source.caching.RequestDistributor;


/**
 * @internal
 */
public class RequestDistributorTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {}

    @AfterClass
    public static void tearDownAfterClass() throws Exception {}

    @Before
    public void setUp() throws Exception {}

    @After
    public void tearDown() throws Exception {}

    @Test(timeout = 11000)
    public void testRequestObject() {
        RequestDistributor<Integer, Integer> t = new RequestDistributor<Integer, Integer>();
        final Integer[] resultArray = new Integer[5];
        final Set<Integer> waitList = new HashSet<Integer>();

        /**
         * Trivial Source
         */
        Source<Integer, Integer> source = new Source<Integer, Integer>() {

            private SourceResponse<Integer> responseAsync = new SourceResponse<Integer>(
                    SourceResponseType.ASYNCHRONOUS, null);
            private ExecutorService exec = Executors.newFixedThreadPool(5);


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
        };

        t.setSource(source);
        SourceListener<Integer, Integer> listener = new SourceListener<Integer, Integer>() {

            @Override
            public void requestCompleted(Integer key, Integer value) {
                if (key < 0 || key >= resultArray.length) {
                    fail("The RequestDistributor retured an invalid key!");
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
                assertNotNull("Value of the Synchronous response was null", response.value);
                resultArray[c] = response.value;
                waitList.remove(new Integer(c));
            }
        }
        
        while (waitList.size() > 0) {
            try {
                this.wait(10);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
    }

}
