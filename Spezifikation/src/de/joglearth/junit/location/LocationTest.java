package de.joglearth.junit.location;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.location.Location;
import de.joglearth.location.LocationType;


public class LocationTest {

    @Test
    public final void testLocation() {
        Location location = new Location(new GeoCoordinates(0.1, 0.1), LocationType.ACTIVITY, "det", "name");
        assertEquals(location.details, "det");
        assertEquals(location.name, "name");
        assertEquals(LocationType.ACTIVITY, location.type);
        assertEquals(new GeoCoordinates(0.1, 0.1), location.point);
    }

}
