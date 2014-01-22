package de.joglearth.junit.geometry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import de.joglearth.geometry.Camera;
import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.Geometry;
import de.joglearth.geometry.SphereGeometry;


public class CameraTest {

    @Test
    public void test() {
        Geometry g = new SphereGeometry();
        Camera cam = new Camera(g);
        double testDistance = 1.9;
        GeoCoordinates testCoord = new GeoCoordinates(-0.345, (Math.PI/4));

        /* variables not null */
        assertNotNull(cam);
        assertNotNull(g);

        /* check distance */
        cam.setDistance(testDistance);
        assertTrue(cam.getDistance() == testDistance);

        /* check position with geographic coordinates */
        cam.setPosition(testCoord);
        assertEquals(cam.getPosition(), testCoord);
        assertTrue(cam.getPosition() != null);

        /* check tilt of the camera */
        try {
            cam.setTilt(0, 0);
        } catch (IllegalArgumentException e) {
            fail("An IllegalArgumentException occurred!");
        }
        
        try {
            cam.setTilt(-1.09382, 0.9363);
        } catch (IllegalArgumentException e) {
            fail("An IllegalArgumentException occurred!");
        }
        
        /* Camera position is set to testCoord */
        assertTrue(cam.isPointVisible(testCoord) == true);
    }
}
