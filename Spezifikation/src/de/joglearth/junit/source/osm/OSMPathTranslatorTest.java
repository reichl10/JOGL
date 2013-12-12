package de.joglearth.junit.source.osm;

import static org.junit.Assert.*;

import org.junit.Test;

import de.joglearth.geometry.Tile;
import de.joglearth.source.osm.OSMPathTranslator;
import de.joglearth.source.osm.OSMTile;
import de.joglearth.surface.TiledMapType;


public class OSMPathTranslatorTest {

    @Test
    public void test() {
        OSMPathTranslator trans = new OSMPathTranslator();
        OSMTile testTile = new OSMTile(new Tile(3, 1, 1), TiledMapType.OSM2WORLD);
        String s = trans.toFileSystemPath(testTile);
        OSMTile tile = trans.fromFileSystemPath(s);
        assertEquals(testTile, tile);
    }

}
