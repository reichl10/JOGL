package de.joglearth.junit.geometry;

import static org.junit.Assert.*;

import org.junit.Test;

import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.PlaneGeometry;
import de.joglearth.geometry.Vector3;


public class PlaneGeometryTest {

    @Test
    public void test() {

        PlaneGeometry geo = new PlaneGeometry();

        /* visible point */
        Vector3 tV1 = new Vector3(1, 1, 1);
        GeoCoordinates gC1 = new GeoCoordinates(0.23, 1);

        /* not visible point */
        Vector3 tV2 = new Vector3(0, 0, 0);
        GeoCoordinates gC2 = new GeoCoordinates(-4.79, 4.79);

        /* check visibility of points */
        assertTrue(geo.isPointVisible(tV1, gC1));
        assertFalse(geo.isPointVisible(tV2, gC2));

        /* check position of a point in space based on geographic coordinates */
        tV2 = geo.getSpacePosition(gC2, 0);
        assertEquals(tV2.x, gC2.getLongitude(), 0.1);
        assertEquals(tV2.y, gC2.getLatitude(), 0.1);
        assertEquals(tV2.z, 0.0, 0.1);

        /* check vector coordinates with geographic coordinates */
        Vector3 tV3= new Vector3(0, 0, -1);
        gC2 = geo.getSurfaceCoordinates(tV1, tV3);
        assertEquals(tV1.x, gC2.getLongitude(), 0.1);
        assertEquals(tV1.y, gC2.getLatitude(), 0.1);
    }
}