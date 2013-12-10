package de.joglearth.junit.source;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;

import org.junit.After;
import org.junit.Test;

import de.joglearth.source.ProgressListener;
import de.joglearth.source.ProgressManager;


public class ProgressManagerTest {
    LinkedList<ProgressListener> listenersToRemove = new LinkedList<ProgressListener>();
    private static final ProgressManager manager = ProgressManager.getInstance();

    @After
    public final void afterTest() {
        ProgressManager manager = ProgressManager.getInstance();
        for (ProgressListener listener : listenersToRemove) {
            manager.removeProgressListener(listener);
        }
        listenersToRemove.clear();
        manager.abortPendingRequests();
    }
    
    
    @Test
    public final void testGetInstance() {
        ProgressManager pm1 = ProgressManager.getInstance();
        ProgressManager pm2 = ProgressManager.getInstance();
        assertSame(pm1, pm2);
    }

    @Test(timeout=1000)
    public final void testAddProgressListener() {
        TestProgressListener listener = new TestProgressListener();
        manager.addProgressListener(listener);
        listenersToRemove.add(listener);
        manager.requestArrived();
        manager.abortPendingRequests();
        while(listener.getUpdateCount() < 1 || listener.getAbortPredingRequestsCount() < 1);
    }

    @Test(timeout=1000)
    public final void testRemoveProgressListener() {
        TestProgressListener listener = new TestProgressListener();
        manager.addProgressListener(listener);
        listenersToRemove.add(listener);
        manager.requestArrived();
        manager.abortPendingRequests();
        while(listener.getUpdateCount() < 1 || listener.getAbortPredingRequestsCount() < 1);
        int oldcountUpdate = listener.getUpdateCount();
        int oldcountReq = listener.getAbortPredingRequestsCount();
        manager.removeProgressListener(listener);
        manager.requestArrived();
        manager.abortPendingRequests();
        assertTrue(oldcountUpdate == listener.getUpdateCount() && oldcountReq == listener.getAbortPredingRequestsCount());
    }

    @Test
    public final void testAbortPendingRequests() {
        TestProgressListener listener = new TestProgressListener();
        manager.addProgressListener(listener);
        listenersToRemove.add(listener);
        manager.abortPendingRequests();
        assertTrue(listener.getAbortPredingRequestsCount() > 0);
    }

    @Test
    public final void testRequestArrivedAndRequestCompleted() {
        TestProgressListener listener = new TestProgressListener();
        manager.addProgressListener(listener);
        listenersToRemove.add(listener);
        manager.requestArrived();
        int updateCount = listener.getUpdateCount();
        assertTrue("RequestArrived seems to fail.", listener.getUpdateCount() > 0);
        assertEquals("RequestArrived seems to fail.", 0.0d, listener.getLastProgress(), 0.0001d);
        manager.requestCompleted();
        assertTrue("RequestCompleted seems to fail.", listener.getUpdateCount() > updateCount);
        assertEquals("RequestCompleted seems to fail.", 1.0d, listener.getLastProgress(), 0.0001d);
    }

    private class TestProgressListener implements ProgressListener {
        private int updateCount = 0;
        private int abortPredingRequestsCount = 0;
        private double lastProgress;
        @Override
        public void updateProgress(double prog) {
            updateCount++;
            lastProgress = prog;
        }

        @Override
        public void abortPendingRequests() {
            abortPredingRequestsCount++;
        }

        /**
         * @return the abortPredingRequestsCount
         */
        public int getAbortPredingRequestsCount() {
            return abortPredingRequestsCount;
        }

        /**
         * @return the updateCount
         */
        public int getUpdateCount() {
            return updateCount;
        }

        /**
         * @return the lastProgress
         */
        public double getLastProgress() {
            return lastProgress;
        }
        
    }
}
