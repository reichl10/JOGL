package de.joglearth.junit.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
import de.joglearth.settings.Settings;
import de.joglearth.settings.SettingsContract;
import de.joglearth.surface.Location;
import de.joglearth.surface.LocationType;


/**
 * @internal
 */
public class SettingsContractTest {

    private static final Boolean TV_BOOLEAN = new Boolean(true);
    private static final Double TV_DOUBLE = new Double(123.301d);
    private static final Float TV_FLOAT = new Float(32.35f);
    private static final Integer TV_INTEGER = new Integer(-5634);
    private static final Long TV_LONG = new Long(34332423);
    private static final String TV_STRING = "jfsdjfisdf*+3439(&2)(/&";
    private static final String TV_LANGUAGE = "GERMAN";
    private static final Set<Location> TV_LOCATIONS = new HashSet<Location>();
    private static final Location TV_LOCATION = new Location(new GeoCoordinates(3.32d, 1.45d), LocationType.USER_TAG,
            "City next to the border", "Passau");
    private static final String TEST_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
            +
            "<settings>\r\n"
            +
            "<entry key=\"exkey1\" type=\"Integer\" value=\"-5634\"/>\r\n"
            +
            "<entry key=\"exkey2\" type=\"Long\" value=\"34332423\"/>\r\n"
            +
            "<entry key=\"exkey3\" type=\"String\" value=\"jfsdjfisdf*+3439(&2)(/&\"/>\r\n"
            +
            "<entry key=\"exkey4\" type=\"Double\" value=\"123.301\"/>\r\n"
            +
            "<entry key=\"exkey5\" type=\"Float\" value=\"32.35\"/>\r\n"
            +
            "<entry key=\"exkey6\" type=\"Boolean\" value=\"true\"/>\r\n"
            +
            "<locations key=\"exkey7\">\r\n"
            +
            "  <location name=\"Name of Location\" details=\"Some Details\" type=\"USER_TAG\">\r\n"
            +
            "    <geocoordinates longitude=\"3.32\" latitude=\"1.45\" />\r\n"
            +
            "  </location>\r\n"
            +
            "  <location name=\"Name of Location 2\" details=\"Some Details\" type=\"USER_TAG\">\r\n"
            +
            "    <geocoordinates longitude=\"3.32\" latitude=\"1.47\" />\r\n" +
            "  </location>\r\n" +
            "</locations>\r\n" +
            "<locations key=\"exkey9\">\r\n" +
            "</locations>\r\n" +
            "</settings>";
    private File settingsFile;


    @Before
    public void setUp() throws Exception {
        String folderName = "joglearth";
        TV_LOCATIONS.add(new Location(new GeoCoordinates(3.32d, 1.45d), LocationType.USER_TAG,
                "City next to the Border", "Passau"));
        TV_LOCATIONS.add(new Location(new GeoCoordinates(3.32d, 1.47d), LocationType.USER_TAG,
                "City in Bavaria", "Regensburg"));
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
        settings.putInteger("exkey1", new Integer(23712));
        settings.putLong("exkey2", new Long(37427373));
        SettingsContract.loadSettings();
        assertEquals(TV_INTEGER, settings.getInteger("exkey1"));
        assertEquals(TV_LONG, settings.getLong("exkey2"));
        assertEquals(TV_STRING, settings.getString("exkey3"));
        assertEquals(TV_DOUBLE, settings.getDouble("exkey4"));
        assertEquals(TV_FLOAT, settings.getFloat("exkey5"));
        assertEquals(TV_BOOLEAN, settings.getBoolean("exkey6"));
        Set<Location> locationSet1 = settings.getLocations("exkey7");
        assertNull(locationSet1);
        for (Location location : locationSet1) {
            assertTrue(TV_LOCATIONS.contains(location));
        }
        Set<Location> locationSet2 = settings.getLocations("exkey9");
        assertTrue(locationSet2.size() == 0);
    }

    @Test
    public void testSaveSettings() {
        Settings settings = Settings.getInstance();
        settings.putInteger(SettingsContract.CACHE_SIZE_FILESYSTEM, TV_INTEGER);
        settings.putInteger(SettingsContract.CACHE_SIZE_MEMORY, TV_INTEGER);
        settings.putBoolean(SettingsContract.ANTIALIASING, TV_BOOLEAN);
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
        assertEquals(TV_BOOLEAN, settings.getBoolean(SettingsContract.ANTIALIASING));
        assertEquals(TV_LANGUAGE, settings.getString(SettingsContract.LANGUAGE));
        assertEquals(TV_BOOLEAN, settings.getLong(SettingsContract.TEXTURE_FILTER));
        Set<Location> locationSet1 = settings.getLocations(SettingsContract.USER_LOCATIONS);
        assertNotNull(locationSet1);
        assertTrue(locationSet1.contains(TV_LOCATION));
        assertTrue(locationSet1.size() == 1);
    }

}
