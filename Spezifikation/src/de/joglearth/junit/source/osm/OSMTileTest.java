package de.joglearth.junit.source.osm;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.joglearth.map.MapConfiguration;
import de.joglearth.map.TileName;
import de.joglearth.map.osm.OSMMapConfiguration;
import de.joglearth.map.osm.OSMMapType;
import de.joglearth.map.osm.OSMTile;


public class OSMTileTest {

    @Test
    public void testOSMTile() {

        MapConfiguration config = new OSMMapConfiguration(OSMMapType.CYCLING);
        OSMTile testTile = new OSMTile(3, 1, 1);
        TileName testName = new TileName(config, testTile);

        assertEquals(testTile, testName.tile);
        assertEquals(OSMMapType.CYCLING, testName.configuration);
    }
}