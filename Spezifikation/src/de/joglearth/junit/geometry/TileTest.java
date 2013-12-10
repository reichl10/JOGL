package de.joglearth.junit.geometry;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.Tile;


public class TileTest {

    @Test
    public void test() {
        Tile testTile1 = new Tile(1, 3, 5);
        Tile testTile2 = new Tile(1, 3, 5);
        
        assertNotNull(testTile1);

        assertTrue(testTile1.getLongitudeIndex() == 3);
        assertTrue(testTile1.getLongitudeIndex() == testTile2.getLongitudeIndex());
        assertTrue(testTile1.getLatitudeIndex() == 5);
        assertTrue(testTile1.getDetailLevel() == 1);
        assertTrue(testTile1.equals(testTile2));

        /*
         * Wir brauchen ein Tile, wo wir die Begrenzungen von Längen & Breitengrad kennen und 1
         * Punkt der darin liegt
         */
        double lon = (testTile1.getLongitudeFrom() + testTile1.getLongitudeTo()) / 2;
        double lat = (testTile1.getLatitudeFrom() + testTile1.getLatitudeTo()) / 2;
        
        GeoCoordinates coords = new GeoCoordinates(lon, lat);
        
        assertTrue(testTile1.contains(coords));
        
    }
}