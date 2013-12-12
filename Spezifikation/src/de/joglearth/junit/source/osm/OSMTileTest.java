package de.joglearth.junit.source.osm;

import static org.junit.Assert.*;

import org.junit.Test;

import de.joglearth.geometry.Tile;
import de.joglearth.source.osm.OSMTile;
import de.joglearth.surface.TiledMapType;


public class OSMTileTest {

    @Test
    public void testOSMTile() {
        Tile t = new Tile(3, 1, 1);
        OSMTile ot = new OSMTile(t, TiledMapType.CYCLING);
        assertEquals(t, ot.tile);
        assertEquals(TiledMapType.CYCLING, ot.type);
    }

}
