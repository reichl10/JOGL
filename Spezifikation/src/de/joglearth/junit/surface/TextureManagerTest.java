package de.joglearth.junit.surface;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.joglearth.geometry.SurfaceListener;
import de.joglearth.geometry.Tile;
import de.joglearth.junit.GLTestWindow;
import de.joglearth.rendering.TextureManager;
import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.SourceResponseType;
import de.joglearth.source.osm.OSMTileName;


public class TextureManagerTest {

    GLTestWindow window;


    @Before
    public final void before() {
        if (window != null)
            window.dispose();
        window = new GLTestWindow();
    }

    @Test
    public final void testTextureManager() {
        TestSource source = new TestSource();
        TextureManager man = new TextureManager(window.getGL(), source, 200000);
    }

    @Test
    public final void testGetTexture() {
        TestSource source = new TestSource();
        TextureManager man = new TextureManager(window.getGL(), source, 200000);
        Integer id = man.getTexture(new Tile(0, 0, 0));
        assertNotNull(id);
        assertTrue(id > 0);
    }

    @Test
    public final void testAddSurfaceListener() {
        TestSource source = new TestSource();
        TextureManager man = new TextureManager(window.getGL(), source, 200000);
        man.addSurfaceListener(new TestSurfaceListener());
    }

    @Test
    public final void testRemoveSurfaceListener() {
        TestSource source = new TestSource();
        TextureManager man = new TextureManager(window.getGL(), source, 200000);
        TestSurfaceListener listener = new TestSurfaceListener();
        man.addSurfaceListener(new TestSurfaceListener());
        man.removeSurfaceListener(listener);
    }


    private class TestSurfaceListener implements SurfaceListener {

        @Override
        public void surfaceChanged(double lonFrom, double latFrom, double lonTo, double latTo) {}
    }
    
    private class TestSource implements Source<OSMTileName, byte[]> {
        private byte[] retByte = {0x3D, 0x00, 0x6F};
        private int requestCount = 0;
        
        @Override
        public SourceResponse<byte[]> requestObject(OSMTileName key,
                SourceListener<OSMTileName, byte[]> sender) {
            requestCount++;
            return new SourceResponse<byte[]>(SourceResponseType.SYNCHRONOUS, retByte);
        }
        
        public int getRequestCount() {
            return requestCount;
        }
    }
}
