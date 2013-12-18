package de.joglearth.junit.geometry;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.joglearth.geometry.Camera;
import de.joglearth.geometry.Geometry;
import de.joglearth.geometry.CameraUtils.GridWalker;
import de.joglearth.geometry.ScreenCoordinates;
import de.joglearth.geometry.SphereGeometry;


public class GridWalkerWhiteBoxTest {
        
    @Test
    public void testSphereWalking() {
        Camera cam = new Camera(new SphereGeometry());
        cam.getGeoCoordinates(new ScreenCoordinates(0.5, 0.5));
        GridWalker walker = new GridWalker(, zoomLevel, camera)
    }

}