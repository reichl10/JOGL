package de.joglearth.junit.source.osm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import de.joglearth.map.MapConfiguration;
import de.joglearth.map.TileName;
import de.joglearth.map.osm.OSMMapConfiguration;
import de.joglearth.map.osm.OSMMapType;
import de.joglearth.map.osm.OSMTile;
import de.joglearth.map.osm.OSMTileManager;
import de.joglearth.settings.SettingsContract;
import de.joglearth.source.SourceListener;


public class OSMTileManagerTest {
    
    @Before
    public final void setUp(){
        SettingsContract.setDefaultSettings();
    }

    @Test
    public void testGetInstance() {       
        OSMTileManager m = OSMTileManager.getInstance();
        assertNotNull(m);
    }

    @Test(timeout = 5000)
    public void testRequestObject() {    
        OSMTileManager m = OSMTileManager.getInstance();
        TestSourceListener l = new TestSourceListener();
        MapConfiguration config = new OSMMapConfiguration(OSMMapType.CYCLING);
        OSMTile testTile = new OSMTile(2, 1, 1);
        TileName testName = new TileName(config, testTile);
        
        m.requestObject(testName, l); 
    }
    
    private class TestSourceListener implements SourceListener<TileName, byte[]> {
        public byte[] tile;        
        MapConfiguration config = new OSMMapConfiguration(OSMMapType.CYCLING);
        OSMTile testTile = new OSMTile(2, 1, 1);
        TileName testName = new TileName(config, testTile);
        
        @Override
        public void requestCompleted(TileName key, byte[] value) {          
            assertEquals(testName, key);
            assertNotNull(tile);
        }
    }
}