package de.joglearth.junit.source.srtm;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.SourceResponseType;
import de.joglearth.source.srtm.SRTMBinarySource;
import de.joglearth.source.srtm.SRTMTileIndex;


public class STRMBinarySourceTest {

    @Test
    public final void testSRTMBinarySource() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public final void testRequestObject() {
        SRTMBinarySource source = new SRTMBinarySource();
        SRTMTileIndex key = new SRTMTileIndex(1, 1);
        TestSourceListener listener = new TestSourceListener(Thread.currentThread());
        SourceResponse<byte[]> response = source.requestObject(key, listener);
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
    private class TestSourceListener implements SourceListener<SRTMTileIndex, byte[]> {
        private Thread waiterThread;
        public TestSourceListener(Thread t) {
            waiterThread = t;
        }
        @Override
        public void requestCompleted(SRTMTileIndex key, byte[] value) {
            waiterThread.notify();
        }
        
    }
}