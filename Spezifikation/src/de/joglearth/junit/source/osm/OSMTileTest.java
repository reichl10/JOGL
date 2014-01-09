package de.joglearth.junit.source.osm;

import static org.junit.Assert.*;

import org.junit.Test;

import de.joglearth.geometry.Tile;
import de.joglearth.map.osm.OSMMapType;
import de.joglearth.source.osm.OSMTileName;


public class OSMTileTest {

    @Test
    public void testOSMTile() {
        Tile t = new Tile(3, 1, 1);
        OSMTileName ot = new OSMTileName(t, OSMMapType.CYCLING);
        assertEquals(t, ot.tile);
        assertEquals(OSMMapType.CYCLING, ot.type);
    }

}
