package de.joglearth.junit.source.osm;

import static org.junit.Assert.*;

import java.io.File;
import java.util.regex.Pattern;

import org.junit.Test;

import de.joglearth.source.osm.OSMTile;
import de.joglearth.source.tiles.osm.OSMPathTranslator;
import de.joglearth.surface.TiledMapType;
import de.joglearth.tiles.Tile;


public class OSMPathTranslatorTest {

    @Test
    public void test() {
        //TODO System.out.println("[0-9a-fA-F]{3}"
            + Pattern.quote(File.separator) + "([A-Z_]+)-([0-9]+)-([0-9]+)-([0-9]+)\\.png");
        OSMPathTranslator trans = new OSMPathTranslator();
        OSMTile testTile = new OSMTile(new Tile(3, 1, 1), TiledMapType.OSM2WORLD);
        String s = trans.toFileSystemPath(testTile);
        OSMTile tile = trans.fromFileSystemPath(s);
        assertEquals(testTile, tile);
        
    }

}
