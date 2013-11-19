package de.joglearth.settings;

import java.util.Set;
import de.joglearth.surface.Location;


/**
 * Used to store settings of JoglEarth. A key can only have one value. If put a
 * value of an other type under the same key it replaces the old value of the
 * other type. For key the <code>null</code> object is not allowed. This Class
 * is thread save.
 */
public final class Settings {

    /**
     * Class to store settings.
     */
    private static Settings instance = null;


    /**
     * Private Constructor to prevent creating an instance. Use
     * {@link #getInstance() getInstance} instead.
     */
    private Settings() {}

    /**
     * Get the instance of Settings.
     * 
     * @return the settings
     */
    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    /**
     * Add a {@link de.joglearth.settings.SettingsListener SettingsListener} to
     * be called if the setting with the given name is changed.
     * 
     * @param key the key of the setting
     * @param listener the listener to be called
     */
    public void addSettingsListener(final String key, final SettingsListener listener) {

    }

    /**
     * Unregisters the given <code>SettingsListener</code> from beeing called if
     * the setting with the given name changes.
     * 
     * @param key the key of the setting
     * @param listener the listener to remove
     */
    public void removeSettingsListener(final String key, final SettingsListener listener) {

    }

    /**
     * Stores a setting of type <code>Integer</code> using a given key.
     * 
     * @param key the key of the setting
     * @param value the value of the setting
     */
    public void putInteger(final String key, final Integer value) {

    }

    /**
     * Stores a setting of type <code>Double</code> using a given key.
     * 
     * @param key the key of the setting
     * @param value the value of the setting
     */
    public void putDouble(final String key, final Double value) {

    }

    /**
     * Stores a setting of type <code>Float</code> using a given key.
     * 
     * @param key the key of the setting
     * @param value the value of the setting
     */
    public void putFloat(final String key, final Float value) {

    }

    /**
     * Stores a setting of type <code>Long</code> using a given key.
     * 
     * @param key the key of the setting
     * @param value the value of the setting
     */
    public void putLong(final String key, final Long value) {

    }

    /**
     * Stores a {@link Location} using a given key.
     * 
     * @param key the locations key
     * @param value the location to add to this key
     */
    public void putLocation(final String key, final Location value) {

    }

    /**
     * Stores a setting of type <code>Boolean</code> using a given key.
     * 
     * @param key the key of the setting
     * @param value the value of the setting
     */
    public void putBoolean(final String key, final Boolean value) {

    }

    /**
     * Stores a setting of type <code>String</code> using a given key.
     * 
     * @param key the key of the setting
     * @param value the value of the setting
     */
    public void putString(final String key, final String value) {

    }

    /**
     * Retrieve the setting stored, using the given key, as <code>Boolean</code>
     * .
     * 
     * @param key the key of the setting
     * @return The setting stored under the given key as <code>Boolean</code> or
     *         <code>null</code> if no setting found with given name or the
     *         setting is no instance of <code>Boolean</code>
     */
    public Boolean getBoolean(final String key) {
        return null;
    }

    /**
     * Retrieve the setting stored, using the given key, as <code>String</code>.
     * 
     * @param key the key of the setting
     * @return The setting stored under the given key as String or
     *         <code>null</code> if no setting found with given name or the
     *         setting is no instance of <code>String</code>
     */
    public String getString(final String key) {
        return null;
    }

    /**
     * Retrieve the setting stored, using the given key, as <code>Long</code>.
     * 
     * @param key the key of the setting
     * @return The setting stored under the given key as <code>Long</code> or
     *         <code>null</code> if no setting found with given name or the
     *         setting is no instance of <code>Long</code>
     */
    public Long getLong(final String key) {
        return null;
    }

    /**
     * Retrieve the setting stored, using the given key, as <code>Float</code>.
     * 
     * @param key the key of the setting
     * @return The setting stored under the given key as <code>Float</code> or
     *         <code>null</code> if no setting found with given name or the
     *         setting is no instance of <code>Float</code>
     */
    public Float getFloat(final String key) {
        return null;
    }

    /**
     * Retrieve the setting stored, using the given key, as <code>Double</code>.
     * 
     * @param key the key of the setting
     * @return The setting stored under the given key as <code>Double</code> or
     *         <code>null</code> if no setting found with given name or the
     *         setting is no instance of <code>Double</code>
     */
    public Double getDouble(final String key) {
        return null;
    }

    /**
     * Retrieve the setting stored, using the given key, as <code>Integer</code>
     * .
     * 
     * @param key the key of the setting
     * @return The setting stored under the given key as <code>Integer</code> or
     *         <code>null</code> if no setting found with given name or the
     *         setting is no instance of <code>Integer</code>
     */
    public Integer getInteger(final String key) {
        return null;
    }

    /**
     * Gets the {@link Location} Objects stored using the given key.
     * 
     * @param key the key to use
     * @return a <code>Set</code> of <code>Location</code> Objects stored under
     *         the given key or <code>null</code> if no <code>Location</code>
     *         Object is found using this key.
     */
    public Set<Location> getLocations(final String key) {
        return null;
    }

}
