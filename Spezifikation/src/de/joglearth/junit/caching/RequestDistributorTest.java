package de.joglearth.junit.caching;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class RequestDistributorTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {}

    @AfterClass
    public static void tearDownAfterClass() throws Exception {}

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {}

    @Test
    public void testRequestObject() {

        /**
         * Trivial Source
         */
        private class IntSource implements Source<Integer, Integer> {

            private SourceResponse response = new SourceResponse(SourceResponseType.SYNCHRONOUS, 33);

            @Override
            public SourceResponse<Integer> requestObject(Integer key,
                SourceListener<Integer, Integer> sender) {


                return response;
            }
        }

        RequestDistributor<Integer, Integer>() t = new RequestDistributor<Integer, Integer>();
        Source source = new IntSource();
        t.setSource(source);
        
        assertSame("Failed to mediate Source!", source.requestObject(3, null), 
                    t.requestObject(3, null));

    }

}
