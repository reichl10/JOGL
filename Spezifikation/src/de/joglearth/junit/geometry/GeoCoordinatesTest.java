package de.joglearth.junit.geometry;

import static org.junit.Assert.*;

import org.junit.Test;

import de.joglearth.geometry.GeoCoordinates;


public class GeoCoordinatesTest {

    @Test
    public void test() {
        GeoCoordinates testCoord1 = new GeoCoordinates(2.44, 0.79);
        GeoCoordinates testCoord2 = new GeoCoordinates(4.79, 2.44);

        /* check geoCoordinate attributes */
        assertFalse(testCoord1.equals(testCoord2));
        testCoord2 = testCoord1.clone();
        assertTrue(testCoord1.equals(testCoord2));

        assertTrue(testCoord1.getLongitude() == 2.44);
        assertFalse(testCoord1.getLatitude() == 0.789999999);

        /* check coordinate parsing */
        try {
            GeoCoordinates.parseCoordinates("55° 17' 48.2\" E", "55° 17' 48.2\" N");
        } catch (IllegalArgumentException e) {
            fail("Wrong format of the parameters.");
        }

        try {
            GeoCoordinates.parseCoordinates("55° 17' 48.2\" E", "55° 17' 48.2\" N");
        } catch (NumberFormatException e) {
            fail("The format of the longitude or latitude is wrong.");
        }
    }
}
