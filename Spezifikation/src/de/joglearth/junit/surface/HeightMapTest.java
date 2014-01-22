package de.joglearth.junit.surface;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.height.srtm.SRTMHeightMap;

public class HeightMapTest {

    @Test
    public final void testGetHeight() {
        double resultVal = 0.0d;
      
        double res = SRTMHeightMap.getInstance().getHeight(new GeoCoordinates(0.2321, 0.521), 0.0d);
        assertEquals(resultVal, res, 0.000001d);
    }
}
