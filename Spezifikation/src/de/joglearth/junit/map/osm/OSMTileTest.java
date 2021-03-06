package de.joglearth.junit.map.osm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.joglearth.map.MapConfiguration;
import de.joglearth.map.TileName;
import de.joglearth.map.osm.OSMMapConfiguration;
import de.joglearth.map.osm.OSMMapType;
import de.joglearth.map.osm.OSMTile;



public class OSMTileTest {

    @Test
    public void testOSMTile() {
    	
        OSMTile t1 = new OSMTile(3, 1, 1);
        assertEquals(3, t1.getDetailLevel());
        assertEquals(1, t1.getLatitudeIndex());
        assertEquals(1, t1.getLatitudeIndex());
        assertTrue(0 < t1.getLatitudeFrom() && t1.getLatitudeFrom() < t1.getLatitudeTo() 
        		&& t1.getLatitudeTo() < Math.PI/2);
        assertTrue(0 > t1.getLongitudeFrom() && t1.getLongitudeFrom() < t1.getLongitudeTo() 
        		&& Math.abs(t1.getLongitudeFrom()) < Math.PI);
        
        
        OSMTile t2 = new OSMTile(3, 1, 1);
        assertTrue(t1.equals(t2));
        assertEquals(t1.hashCode(), t2.hashCode());
        
        OSMTile t3 = new OSMTile(5, 3, 3);
        assertTrue(t1.hashCode() > t3.hashCode() || t1.hashCode() < t3.hashCode());
        
        OSMTile n = (OSMTile) t3.getScaledAlternative().tile;
        assertTrue(t3.getDetailLevel() > n.getDetailLevel());
        

        MapConfiguration config = new OSMMapConfiguration(OSMMapType.CYCLING);
        OSMTile testTile = new OSMTile(3, 1, 1);
        TileName testName = new TileName(config, testTile);

        assertEquals(testTile, testName.tile);
        assertEquals(config, testName.configuration);
    }
}