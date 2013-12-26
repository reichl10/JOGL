package de.joglearth.junit.geometry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import de.joglearth.geometry.Camera;
import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.CameraGeometry;
import de.joglearth.geometry.SphereGeometry;


public class CameraTest {

    @Test
    public void test() {
        CameraGeometry g = new SphereGeometry();
        Camera cam = new Camera(g);
        double testDistance = 1.9;
        GeoCoordinates testCoord = new GeoCoordinates(-0.345, (Math.PI/4));

        assertNotNull(cam);
        assertNotNull(g);

        cam.setDistance(testDistance);
        assertTrue(cam.getDistance() == testDistance);


        cam.setPosition(testCoord);
        assertEquals(cam.getPosition(), testCoord);
        assertTrue(cam.getPosition() != null);

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
