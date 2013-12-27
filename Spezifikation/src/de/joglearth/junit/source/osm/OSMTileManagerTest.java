package de.joglearth.junit.source.osm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import de.joglearth.geometry.Tile;
import de.joglearth.source.SourceListener;
import de.joglearth.source.osm.OSMTileName;
import de.joglearth.source.tiles.osm.OSMMapType;
import de.joglearth.source.tiles.osm.OSMTileManager;


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
        m.requestObject(new OSMTileName(new Tile(2, 1, 1), OSMMapType.CYCLING), l);
        while(!l.gotAnswer);
        assertNotNull(l.tile);
    }
    private class TestSourceListener implements SourceListener<OSMTileName, byte[]> {
        public byte[] tile;
        public boolean gotAnswer = false;
        OSMTileName t = new OSMTileName(new Tile(2, 1, 1), OSMMapType.CYCLING);
        @Override
        public void requestCompleted(OSMTileName key, byte[] value) {
            assertEquals(t, key);
            gotAnswer = true;
        }
    }
}
