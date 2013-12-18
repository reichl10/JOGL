package de.joglearth.junit.source.srtm;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.junit.Test;

import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.SourceResponseType;
import de.joglearth.source.srtm.SRTMTile;
import de.joglearth.source.srtm.SRTMTileIndex;
import de.joglearth.source.srtm.SRTMTileManager;


public class STRMTileManager {

    @Test
    public final void testGetInstance() {
        SRTMTileManager man1 = SRTMTileManager.getInstance();
        SRTMTileManager man2 = SRTMTileManager.getInstance();
        assertSame(man1, man2);
    }

    @Test
    public final void testRequestObject() {
        SRTMTileManager manager = SRTMTileManager.getInstance();
        TestSourceListener listener = new TestSourceListener(Thread.currentThread());
        SourceResponse<SRTMTile> response = manager.requestObject(new SRTMTileIndex(1, 1), null);
        if (response.response == SourceResponseType.MISSING) {
            fail("We need to Have a SRTMTile :(");
        } else if (response.response == SourceResponseType.SYNCHRONOUS) {
            assertNotNull(response.value);
        } else {
            synchronized (this) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class TestSourceListener implements SourceListener<SRTMTileIndex, SRTMTile> {
        private Thread waiterThread;
        public TestSourceListener(Thread t) {
            waiterThread = t;
        }
        @Override
        public void requestCompleted(SRTMTileIndex key, SRTMTile value) {
            waiterThread.notify();
        }
        
    }
}
