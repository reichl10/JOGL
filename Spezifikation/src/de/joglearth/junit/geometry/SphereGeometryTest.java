package de.joglearth.junit.geometry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.SphereGeometry;
import de.joglearth.geometry.Vector3;


public class SphereGeometryTest {

    @Test
    public void test() {

        SphereGeometry geo = new SphereGeometry();

        /* visible point */
        Vector3 tV1 = new Vector3(0, 0, 2);
        GeoCoordinates gC1 = new GeoCoordinates(0.23, 1);

        /* not visible point */
        Vector3 tV2 = new Vector3(0, 0, 3);
        GeoCoordinates gC2 = new GeoCoordinates(Math.PI, 0.0);

        /* check visibility of points */
        assertTrue(geo.isPointVisible(tV1, gC1));
        assertFalse(geo.isPointVisible(tV2, gC2));

        /* check position of the point in space */
        tV2 = geo.getSpacePosition(gC2, 0);
        assertEquals(tV2.x, 0.0, 0.1);
        assertEquals(tV2.y, 0.0, 0.1);
        assertEquals(tV2.z, -1.0, 0.1);

        /*
         * check vector coordinates with geographic coordinates; camera position = tV1; viewVector =
         * tV3; gC2 is the same point at the back of the earth
         */
        Vector3 tV3 = new Vector3(0, 0, -1);
        gC2 = geo.getSurfaceCoordinates(tV1, tV3);
        assertEquals(tV1.x, gC2.longitude, 0.1);
        assertEquals(tV1.y, gC2.latitude, 0.1);
    }
}
