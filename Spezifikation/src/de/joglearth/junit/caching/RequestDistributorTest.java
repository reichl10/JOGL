package de.joglearth.junit.caching;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

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
        RequestDistributor<Integer, Integer> t = new RequestDistributor<Integer, Integer>();

        /**
         * Trivial Source
         */
       Source<Integer, Integer> source = new Source<Integer, Integer>(){

            private SourceResponse<Integer> response = new SourceResponse<Integer>(SourceResponseType.SYNCHRONOUS, 33);

            @Override
            public SourceResponse<Integer> requestObject(Integer key,
                SourceListener<Integer, Integer> sender) {


                return response;
            }
        };

        t.setSource(source);
        
        assertSame("Failed to mediate Source!", source.requestObject(3, null), 
                    t.requestObject(3, null));

    }

}
