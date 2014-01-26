package de.joglearth.junit.geometry;

import static org.junit.Assert.*;

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
    	
    	assertEquals(geo1.longitude, lin.projectLongitude(geo1.longitude), 0.1);
    	assertEquals(geo1.latitude, lin.projectLatitude(geo1.latitude), 0.1);
    	
    	assertEquals(geo1.longitude, merc.projectLongitude(geo1.longitude), 0.1);
    	assertEquals(geo1.latitude, merc.projectLatitude(geo1.latitude), 0.1);
    	
    	assertEquals(3.34d, merc.projectLatitude(geo2.latitude), 0.1);
    	assertEquals(3.34d, merc.projectLatitude(geo2.latitude + 2.0 * Math.PI), 0.1);
    	
       
    }
}
