package de.joglearth.junit.source.opengl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import de.joglearth.geometry.Tile;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.SourceResponseType;
import de.joglearth.source.opengl.TileTextureSource;


public class TileTextureSourceTest {

    @Test(timeout=10000)
    public final void testRequestObject() {
        TileTextureSource source = new TileTextureSource();
        Tile key = new Tile(0, 0, 0);
        TestSourceListener listener = new TestSourceListener(Thread.currentThread());
        SourceResponse<Integer> response = source.requestObject(key, listener);
        if (response.response == SourceResponseType.MISSING) {
            fail("We need to Have a Texture :(");
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
    private class TestSourceListener implements SourceListener<Tile, Integer> {
        private Thread waiterThread;
        public TestSourceListener(Thread t) {
            waiterThread = t;
        }
        @Override
        public void requestCompleted(Tile key, Integer value) {
            waiterThread.notify();
        }
        
    }
}
