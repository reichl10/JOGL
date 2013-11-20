package de.joglearth.junit.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import de.joglearth.settings.Settings;
import de.joglearth.settings.SettingsContract;


public class WriteAndLoadSettings {

    private static final Boolean TV_BOOLEAN = new Boolean(false);
    private static final Double  TV_DOUBLE  = new Double(123.301d);
    private static final Float   TV_FLOAT   = new Float(32.35f);
    private static final Integer TV_INTEGER = new Integer(39424);
    private static final Long    TV_LONG    = new Long(348324023);
    private static final String  TV_STRING  = "jfsdjfisdf*+3439(&2§)(/&";


    @Before
    public void setUp() throws Exception {}

    @After 
    public void tearDown() throws Exception {}

    @Test
    public void testLoadSettings() {
        Settings settings = Settings.getInstance();
        settings.putBoolean("tkBoolean", TV_BOOLEAN);
        settings.putDouble("tkDouble", TV_DOUBLE);
        settings.putFloat("tkFloat", TV_FLOAT);
        settings.putInteger("tkInteger", TV_INTEGER);
        settings.putLong("tkLong", TV_LONG);
        settings.putString("tkString", TV_STRING);
        SettingsContract.saveSettings();
        settings.putBoolean("tkBoolean", null);
        settings.putDouble("tkDouble", null);
        settings.putFloat("tkFloat", null);
        settings.putInteger("tkInteger", null);
        settings.putLong("tkLong", null);
        settings.putString("tkString", null);
        SettingsContract.loadSettings();
        assertEquals("Loading Boolean Failed", TV_BOOLEAN, settings.getBoolean("tkBoolean"));
        assertEquals("", TV_DOUBLE, settings.getDouble("tkDouble"));
        assertEquals("", TV_FLOAT, settings.getFloat("tkFloat"));
        assertEquals(TV_INTEGER, settings.getInteger("tkInteger"));
        assertEquals(TV_LONG, settings.getLong("tkLong"));
        assertEquals(TV_STRING, settings.getString("tkString"));
    }

    @Test
    public void testSaveSettings() {
        fail("Not yet implemented.");
    }

}
