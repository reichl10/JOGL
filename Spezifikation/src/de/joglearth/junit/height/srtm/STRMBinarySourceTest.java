package de.joglearth.junit.height.srtm;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import de.joglearth.height.srtm.SRTMBinarySource;
import de.joglearth.height.srtm.SRTMTileName;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.SourceResponseType;


public class STRMBinarySourceTest {

    @Test
    public final void testSRTMBinarySource() {
        //TODO
    }

    @Test
    public final void testRequestObject() {
        SRTMBinarySource source = new SRTMBinarySource();
        SRTMTileName key = new SRTMTileName(1, 1);
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
    private class TestSourceListener implements SourceListener<SRTMTileName, byte[]> {
        private Thread waiterThread;
        public TestSourceListener(Thread t) {
            waiterThread = t;
        }
        @Override
        public void requestCompleted(SRTMTileName key, byte[] value) {
            waiterThread.notify();
        }
        
    }
}
