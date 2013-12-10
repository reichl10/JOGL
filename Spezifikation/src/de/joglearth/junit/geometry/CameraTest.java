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
        GeoCoordinates testCoord = new GeoCoordinates(-0.345, (Math.PI/2));

        assertNotNull(cam);
        assertNotNull(g);

        try {
            cam.setGeometry(null);
        } catch (IllegalArgumentException e) {
            fail("An IllegalArgumentException occurred!");
        }

        cam.setDistance(testDistance);
        assertTrue(cam.getDistance() == testDistance);

        try {
            cam.setDistance(0);
        } catch (IllegalArgumentException e) {
            fail("An IllegalArgumentException occurred!");
        }

        cam.setPosition(testCoord);
        assertEquals(cam.getPosition(), testCoord);
        assertTrue(cam.getPosition() != null);

        try {
            cam.setPosition(null);
        } catch (IllegalArgumentException e) {
            fail("An IllegalArgumentException occurred!");
        }

        try {
            cam.setTilt(0, 0);
        } catch (IllegalArgumentException e) {
            fail("An IllegalArgumentException occurred!");
        }
        
        try {
            cam.setTilt(45.9382, 711.9363);
        } catch (IllegalArgumentException e) {
            fail("An IllegalArgumentException occurred!");
        }
        
        /* Camera position is set to testCoord */
        assertTrue(cam.isPointVisible(testCoord) == true);
    }
}