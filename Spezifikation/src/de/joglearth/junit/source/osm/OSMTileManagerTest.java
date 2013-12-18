package de.joglearth.junit.source.osm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import de.joglearth.geometry.Tile;
import de.joglearth.source.SourceListener;
import de.joglearth.source.osm.OSMTile;
import de.joglearth.source.osm.OSMTileManager;
import de.joglearth.surface.TiledMapType;


public class OSMTileManagerTest {

    @Test
    public void testGetInstance() {
        OSMTileManager m = OSMTileManager.getInstance();
        assertNotNull(m);
    }

    @Test(timeout = 5000)
    public void testRequestObject() {
        OSMTileManager m = OSMTileManager.getInstance();
        TestSourceListener l = new TestSourceListener();
        m.requestObject(new OSMTile(new Tile(2, 1, 1), TiledMapType.CYCLING), l);
        while(!l.gotAnswer);
        assertNotNull(l.tile);
    }
    private class TestSourceListener implements SourceListener<OSMTile, byte[]> {
        public byte[] tile;
        public boolean gotAnswer = false;
        OSMTile t = new OSMTile(new Tile(2, 1, 1), TiledMapType.CYCLING);
        @Override
        public void requestCompleted(OSMTile key, byte[] value) {
            assertEquals(t, key);
            gotAnswer = true;
        }
    }
}
