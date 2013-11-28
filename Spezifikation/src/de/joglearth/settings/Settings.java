package de.joglearth.settings;

import java.util.Set;
import de.joglearth.surface.Location;


/**
 * Used to store settings of JoglEarth. A key can only have one value, except for Locations where
 * multiple can exist under the same key. If put a value of an other type under the same key it
 * replaces the old value of the other type. For key the <code>null</code> object is not allowed.
 * This class is thread-safe.
 */
public final class Settings {

    /**
     * Class to store settings.
     */
    private static Settings instance = null;


    /**
     * Private Constructor to prevent creating an instance. Use {@link #getInstance()}
     * instead.
     */
    private Settings() {

    }

    /**
     * Get the instance of Settings.
     * 
     * @return The settings
     */
    public static synchronized Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    /**
     * Add a {@link de.joglearth.settings.SettingsListener} to be called if the setting with the given name is changed.
     * 
     * @param key The key of the setting
     * @param listener The listener to be called
     */
    public void addSettingsListener(final String key, final SettingsListener listener) {

    }

    /**
     * Unregisters the given {@link de.joglearth.settings.SettingsListener} from being called if the setting with the
     * given name changes.
     * 
     * @param key The key of the setting
     * @param listener The listener to remove
     */
    public void removeSettingsListener(final String key, final SettingsListener listener) {

    }

    /**
     * Stores a setting of type <code>Integer</code> using a given key.
     * 
     * @param key The key of the setting
     * @param value The value of the setting
     */
    public synchronized void putInteger(final String key, final Integer value) {

    }

    /**
     * Stores a setting of type <code>Double</code> using a given key.
     * 
     * @param key The key of the setting
     * @param value The value of the setting
     */
    public synchronized void putDouble(final String key, final Double value) {

    }

    /**
     * Stores a setting of type <code>Float</code> using a given key.
     * 
     * @param key The key of the setting
     * @param value The value of the setting
     */
    public synchronized void putFloat(final String key, final Float value) {

    }

    /**
     * Stores a setting of type <code>Long</code> using a given key.
     * 
     * @param key The key of the setting
     * @param value The value of the setting
     */
    public synchronized void putLong(final String key, final Long value) {

    }

    /**
     * Stores a {@link de.joglearth.surface.Location} using a given key.
     * 
     * @param key The locations key
     * @param value The location to add to this key
     */
    public synchronized void putLocation(final String key, final Location value) {

    }

    /**
     * Removes the given {@link de.joglearth.surface.Location} from the given key.
     * The Location that is removed is found by <code>this == value || this.equals(value)</code>
     * 
     * @param key The key the {@link de.joglearth.surface.Location} should be removed from
     * @param value The <code>Location</code> to remove
     */
    public synchronized void dropLocation(final String key, final Location value) {}

    /**
     * Stores a setting of type <code>Boolean</code> using a given key.
     * 
     * @param key The key of the setting
     * @param value The value of the setting
     */
    public synchronized void putBoolean(final String key, final Boolean value) {

    }

    /**
     * Stores a setting of type <code>String</code> using a given key.
     * 
     * @param key The key of the setting
     * @param value The value of the setting
     */
    public synchronized void putString(final String key, final String value) {

    }

    /**
     * Retrieve the setting stored, using the given key, as <code>Boolean</code> .
     * 
     * @param key The key of the setting
     * @return The setting stored under the given key as <code>Boolean</code> or <code>null</code>
     *         if no setting found with given name or the setting is no instance of
     *         <code>Boolean</code>
     */
    public synchronized Boolean getBoolean(final String key) {
        return null;
    }

    /**
     * Retrieve the setting stored, using the given key, as <code>String</code>.
     * 
     * @param key The key of the setting
     * @return The setting stored under the given key as String or <code>null</code> if no setting
     *         found with given name or the setting is no instance of <code>String</code>
     */
    public synchronized String getString(final String key) {
        return null;
    }

    /**
     * Retrieve the setting stored, using the given key, as <code>Long</code>.
     * 
     * @param key The key of the setting
     * @return The setting stored under the given key as <code>Long</code> or <code>null</code> if
     *         no setting found with given name or the setting is no instance of <code>Long</code>
     */
    public synchronized Long getLong(final String key) {
        return null;
    }

    /**
     * Retrieve the setting stored, using the given key, as <code>Float</code>.
     * 
     * @param key The key of the setting
     * @return The setting stored under the given key as <code>Float</code> or <code>null</code> if
     *         no setting found with given name or the setting is no instance of <code>Float</code>
     */
    public synchronized Float getFloat(final String key) {
        return null;
    }

    /**
     * Retrieve the setting stored, using the given key, as <code>Double</code>.
     * 
     * @param key The key of the setting
     * @return The setting stored under the given key as <code>Double</code> or <code>null</code> if
     *         no setting found with given name or the setting is no instance of <code>Double</code>
     */
    public synchronized Double getDouble(final String key) {
        return null;
    }

    /**
     * Retrieve the setting stored, using the given key, as <code>Integer</code> .
     * 
     * @param key The key of the setting
     * @return The setting stored under the given key as <code>Integer</code> or <code>null</code>
     *         if no setting found with given name or the setting is no instance of
     *         <code>Integer</code>
     */
    public synchronized Integer getInteger(final String key) {
        return null;
    }

    /**
     * Gets the {@link de.joglearth.surface.Location} objects stored using the given key.
     * 
     * @param key The key to use
     * @return A <code>Set</code> of <code>Location</code> objects stored under the given key or
     *         <code>null</code> if no <code>Location</code> object is found using this key
     */
    public synchronized Set<Location> getLocations(final String key) {
        return null;
    }

}
