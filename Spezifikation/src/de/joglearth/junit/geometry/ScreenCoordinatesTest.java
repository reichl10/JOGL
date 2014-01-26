package de.joglearth.junit.geometry;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.joglearth.geometry.ScreenCoordinates;


public class ScreenCoordinatesTest {

    @Test
    public void test() {
        ScreenCoordinates testCoord1 = new ScreenCoordinates(0, 0);
        ScreenCoordinates testCoord2= new ScreenCoordinates(0.33, 0.88);
        
        /* check screenCoordinates attributes */
        assertFalse(testCoord1.equals(testCoord2));
    }
}
