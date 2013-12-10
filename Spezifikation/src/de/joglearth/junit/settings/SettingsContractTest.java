package de.joglearth.junit.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.rendering.AntialiasingType;
import de.joglearth.settings.Settings;
import de.joglearth.settings.SettingsContract;
import de.joglearth.surface.Location;
import de.joglearth.surface.LocationType;


/**
 * @internal
 */
public class SettingsContractTest {

    private static final Boolean TV_BOOLEAN = new Boolean(true);
    private static final Integer TV_INTEGER = new Integer(-5634);
    private static final String TV_LANGUAGE = "GERMAN";
    private static final Set<Location> TV_LOCATIONS = new HashSet<Location>();
    private static final Location TV_LOCATION = new Location(new GeoCoordinates(3.32d, 1.45d),
            LocationType.USER_TAG,
            "City next to the border", "Passau");
    private static final String TEST_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<settings>\n" +
            "<entry key=\"LevelOfDetail\" type=\"String\" value=\"MEDIUM\"/>\n" +
            "<entry key=\"Language\" type=\"String\" value=\"GERMAN\"/>\n" +
            "<entry key=\"TextureFilter\" type=\"Boolean\" value=\"false\"/>\n" +
            "<locations key=\"UserLocations\">\n" +
            "  <location name=\"Passau\" details=\"City next to the border\" type=\"USER_TAG\">\n" +
            "    <geocoordinates longitude=\"0.84764824586968\" latitude=\"0.23503767841967\" />\n"
            +
            "  </location>\n" +
            "</locations>\n" +
            "</settings>";
    private File settingsFile;


    @Before
    public void setUp() throws Exception {
        String folderName = "joglearth";
        TV_LOCATIONS.add(new Location(new GeoCoordinates(0.84764824586968d, 0.23503767841967d),
                LocationType.USER_TAG,
                "City next to the border", "Passau"));
        String os = System.getProperty("os.name");
        if (os.contains("Windows")) {
            String localAppdata = System.getenv("LOCALAPPDATA");
            settingsFile = new File(localAppdata + "\\" + folderName + "\\" + "settings.xml");
        } else if (os.contains("Linux")) {
            String userHome = System.getProperty("user.home");
            settingsFile = new File(userHome + File.separator + "." + folderName + File.separator
                    + "settings.xml");
        } else {
            settingsFile = null;
            fail("This System is not supported!");
            return;
        }
        if (!settingsFile.exists()) {
            settingsFile.mkdirs();
        }
        FileOutputStream fos = new FileOutputStream(settingsFile);
        fos.write(TEST_XML.getBytes(StandardCharsets.UTF_8));
        fos.close();
    }

    @After
    public void tearDown() throws Exception {
        if (settingsFile != null) {
            settingsFile.delete();
        }
    }

    @Test
    public void testLoadSettings() {
        Settings settings = Settings.getInstance();
        settings.putInteger(SettingsContract.CACHE_SIZE_FILESYSTEM, new Integer(23712));
        settings.putString(SettingsContract.LEVEL_OF_DETAILS, "WrongValue");
        SettingsContract.loadSettings();
        assertEquals("MEDIUM", settings.getString(SettingsContract.LEVEL_OF_DETAILS));
        assertEquals("GERMAN", settings.getString(SettingsContract.LANGUAGE));
        assertEquals(new Boolean(false), settings.getBoolean(SettingsContract.TEXTURE_FILTER));
        Set<Location> locationSet1 = settings.getLocations(SettingsContract.USER_LOCATIONS);
        assertNotNull(locationSet1);
        for (Location location : locationSet1) {
            assertTrue(TV_LOCATIONS.contains(location));
        }
    }

    @Test
    public void testSaveSettings() {
        Settings settings = Settings.getInstance();
        settings.putInteger(SettingsContract.CACHE_SIZE_FILESYSTEM, TV_INTEGER);
        settings.putInteger(SettingsContract.CACHE_SIZE_MEMORY, TV_INTEGER);
        settings.putString(SettingsContract.ANTIALIASING, AntialiasingType.MSAA_2.name());
        settings.putString(SettingsContract.LANGUAGE, TV_LANGUAGE);
        settings.putBoolean(SettingsContract.TEXTURE_FILTER, TV_BOOLEAN);
        settings.putLocation(SettingsContract.USER_LOCATIONS, TV_LOCATION);
        SettingsContract.saveSettings();
        settings.putInteger(SettingsContract.CACHE_SIZE_FILESYSTEM, null);
        settings.putInteger(SettingsContract.CACHE_SIZE_MEMORY, null);
        settings.putBoolean(SettingsContract.ANTIALIASING, null);
        settings.putString(SettingsContract.LANGUAGE, null);
        settings.putBoolean(SettingsContract.TEXTURE_FILTER, null);
        SettingsContract.loadSettings();
        assertEquals(TV_INTEGER, settings.getInteger(SettingsContract.CACHE_SIZE_FILESYSTEM));
        assertEquals(TV_INTEGER, settings.getInteger(SettingsContract.CACHE_SIZE_MEMORY));
        assertEquals(AntialiasingType.MSAA_2.name(),
                settings.getString(SettingsContract.ANTIALIASING));
        assertEquals(TV_LANGUAGE, settings.getString(SettingsContract.LANGUAGE));
        assertEquals(TV_BOOLEAN, settings.getLong(SettingsContract.TEXTURE_FILTER));
        Set<Location> locationSet1 = settings.getLocations(SettingsContract.USER_LOCATIONS);
        assertNotNull(locationSet1);
        assertTrue(locationSet1.contains(TV_LOCATION));
        assertTrue(locationSet1.size() == 1);
    }

}
