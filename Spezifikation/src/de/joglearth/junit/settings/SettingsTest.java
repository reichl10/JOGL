package de.joglearth.junit.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.location.Location;
import de.joglearth.location.LocationType;
import de.joglearth.opengl.Antialiasing;
import de.joglearth.settings.Settings;
import de.joglearth.settings.SettingsContract;


public class SettingsTest {

    private static final Integer  TEST_INTEGER  = new Integer(32789);
    private static final Location TEST_LOCATION = new Location(new GeoCoordinates(3.32d, 1.45d),
                                                        LocationType.USER_TAG,
                                                        "City next to the border", "Passau");
    private static Settings       s;


    @Before
    public void setUp() throws Exception {
        s = Settings.getInstance();
    }

    @After
    public void tearDown() throws Exception {

    }

    // Nicht verwendete Methoden der Settings: putFloat(), putDouble(), getFloat(), getDouble(),
    // putLong(), getLong()

    @Test
    public void test() throws Exception {
        s.putInteger(SettingsContract.CACHE_SIZE_MEMORY, TEST_INTEGER);
        assertEquals(TEST_INTEGER, s.getInteger(SettingsContract.CACHE_SIZE_MEMORY));

        s.putBoolean(SettingsContract.TEXTURE_FILTER, true);
        assertEquals(true, s.getBoolean(SettingsContract.TEXTURE_FILTER));

        s.putString(SettingsContract.ANTIALIASING, Antialiasing.MSAA_2X.name());
        assertEquals(Antialiasing.MSAA_2X.name(), s.getString(SettingsContract.ANTIALIASING));

        s.putLocation(SettingsContract.USER_LOCATIONS, TEST_LOCATION);
        Set<Location> locationSet = s.getLocations(SettingsContract.USER_LOCATIONS);
        assertNotNull(locationSet);
        assertTrue(locationSet.contains(TEST_LOCATION));
        assertTrue(locationSet.size() == 1);
        s.dropLocation(SettingsContract.USER_LOCATIONS, TEST_LOCATION);
        locationSet = s.getLocations(SettingsContract.USER_LOCATIONS);
        assertTrue(locationSet.size() == 0);
        
        try {
            s.putString(SettingsContract.CACHE_SIZE_FILESYSTEM, null);
        } catch(IllegalArgumentException e) {
            fail("An IllegalArgumentException occurred!");
        }
    }
}