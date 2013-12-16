package de.joglearth.junit.surface;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.surface.HeightMap;


public class HeightMapTest {

    @Test
    public final void testGetHeight() {
        double resultVal = 0.0d;
        double res = HeightMap.getHeight(new GeoCoordinates(0.2321, 0.521));
        assertEquals(resultVal, res, 0.000001d);
    }
}
