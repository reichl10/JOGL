package de.joglearth.junit.geometry;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.map.osm.OSMTile;


public class TileTest {

    @Test
    public void test() {
        OSMTile testTile1 = new OSMTile(3, 3, 5);
        OSMTile testTile2 = new OSMTile(3, 3, 5);
        
        assertNotNull(testTile1);

        /* check tiles attributes */
        assertTrue(testTile1.getLongitudeIndex() == 3);
        assertTrue(testTile1.getLongitudeIndex() == testTile2.getLongitudeIndex());
        assertTrue(testTile1.getLatitudeIndex() == 5);
        assertTrue(testTile1.getDetailLevel() == 3);
        assertTrue(testTile1.equals(testTile2));

        /* check if a given point is within the tile */
        double lon = (testTile1.getLongitudeFrom() + testTile1.getLongitudeTo()) / 2;
        double lat = (testTile1.getLatitudeFrom() + testTile1.getLatitudeTo()) / 2;
        GeoCoordinates coords = new GeoCoordinates(lon, lat);
        assertTrue(testTile1.contains(coords));
    }
}