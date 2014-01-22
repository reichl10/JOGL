package de.joglearth.junit.geometry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.LinearProjection;
import de.joglearth.geometry.MapProjection;
import de.joglearth.geometry.MercatorProjection;


public class ProjectionTest {
	
    @Test
    public void test() {

    	double lon = 1;
    	double lat1 = 0;
    	double lat2 = 1.5;
    	GeoCoordinates geo1 = new GeoCoordinates(lon, lat1);
    	GeoCoordinates geo2 = new GeoCoordinates(lon, lat2);
    	
    	MapProjection lin = new LinearProjection();
    	MapProjection merc = new MercatorProjection();
    	
    	assertEquals(geo1.getLongitude(), lin.projectLongitude(geo1.getLongitude()), 0.1);
    	assertEquals(geo1.getLatitude(), lin.projectLatitude(geo1.getLatitude()), 0.1);
    	
    	assertEquals(geo1.getLongitude(), merc.projectLongitude(geo1.getLongitude()), 0.1);
    	assertEquals(geo1.getLatitude(), merc.projectLatitude(geo1.getLatitude()), 0.1);
    	
    	assertEquals(3.34d, merc.projectLatitude(geo2.getLatitude()), 0.1);
    	assertEquals(3.34d, merc.projectLatitude(geo2.getLatitude() + 2.0 * Math.PI), 0.1);
    	
       
    }
}
