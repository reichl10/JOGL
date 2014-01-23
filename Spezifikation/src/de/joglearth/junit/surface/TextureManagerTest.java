package de.joglearth.junit.surface;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.joglearth.geometry.LinearProjection;
import de.joglearth.geometry.SurfaceListener;
import de.joglearth.geometry.Tile;
import de.joglearth.height.flat.FlatHeightMap;
import de.joglearth.junit.GLTestWindow;
import de.joglearth.rendering.ProjectedTile;
import de.joglearth.rendering.TextureManager;
import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.SourceResponseType;
import de.joglearth.map.MapConfiguration;
import de.joglearth.map.TileName;
import de.joglearth.map.osm.OSMMapConfiguration;
import de.joglearth.map.osm.OSMMapType;
import de.joglearth.map.osm.OSMTile;
import de.joglearth.opengl.TransformedTexture;


public class TextureManagerTest {

    GLTestWindow window;
    MapConfiguration mapconf = new OSMMapConfiguration(OSMMapType.SATELLITE);


    @Before
    public final void before() {
        if (window != null)
            window.dispose();
        window = new GLTestWindow();
    }

    @Test
    
    public final void testTextureManager() {
        TestSource source = new TestSource();
        TextureManager man = new TextureManager((GLContext) window.getEasel(), 200000, mapconf);
    }

    @Test
    public final void testGetTexture() {
        TestSource source = new TestSource();
        TextureManager man = new TextureManager((GLContext) window.getEasel(), 200000, mapconf);
        OSMTile t = new OSMTile(2, 1, 0);
        int subdivision = 1;
        ProjectedTile lin = new ProjectedTile(t, new LinearProjection(), 0,
        		subdivision, FlatHeightMap.getInstance());
        TransformedTexture id = man.getTexture(lin.tile, 3);
        assertNotNull(id);
        
        //more testing...
        
        //assertTrue(id > 0);
    }

    @Test
    public final void testAddSurfaceListener() {
        TestSource source = new TestSource();
        TextureManager man = new TextureManager((GLContext) window.getEasel(), 200000, mapconf);
        man.addSurfaceListener(new TestSurfaceListener());
    }

    @Test
    public final void testRemoveSurfaceListener() {
        TestSource source = new TestSource();
        TextureManager man = new TextureManager((GLContext) window.getEasel(), 200000, mapconf);
        TestSurfaceListener listener = new TestSurfaceListener();
        man.addSurfaceListener(new TestSurfaceListener());
        man.removeSurfaceListener(listener);
    }


    private class TestSurfaceListener implements SurfaceListener {

        @Override
        public void surfaceChanged(double lonFrom, double latFrom, double lonTo, double latTo) {}
    }
    
    private class TestSource implements Source<TileName, byte[]> {
        private byte[] retByte = {0x3D, 0x00, 0x6F};
        private int requestCount = 0;
        
        @Override
        public SourceResponse<byte[]> requestObject(TileName key,
                SourceListener<TileName, byte[]> sender) {
            requestCount++;
            return new SourceResponse<byte[]>(SourceResponseType.SYNCHRONOUS, retByte);
        }
        
        public int getRequestCount() {
            return requestCount;
        }

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}
    }
}
