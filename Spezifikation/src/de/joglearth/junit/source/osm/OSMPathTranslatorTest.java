package de.joglearth.junit.source.osm;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import de.joglearth.map.MapConfiguration;
import de.joglearth.map.TileName;
import de.joglearth.map.osm.OSMMapConfiguration;
import de.joglearth.map.osm.OSMMapType;
import de.joglearth.map.osm.OSMPathTranslator;
import de.joglearth.map.osm.OSMTile;


public class OSMPathTranslatorTest {

    @Test
    public void test() {

        OSMPathTranslator trans = new OSMPathTranslator();
        MapConfiguration config = new OSMMapConfiguration(OSMMapType.OSM2WORLD);
        OSMTile testTile = new OSMTile(3, 1, 1);
        TileName testName = new TileName(config, testTile);
        
        String s = trans.toFileSystemPath(testName);
        TileName resultTileName = trans.fromFileSystemPath(s);
        assertEquals(testName, resultTileName);
    }
}
