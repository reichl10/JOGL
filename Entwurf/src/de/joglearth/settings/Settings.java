package de.joglearth.settings;

import javax.tools.JavaFileManager.Location;



/**
 * Settings
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
     * Add a SettingsListener to be called if setting with the given name is
     * changed.
     * 
     * @param property
     *            the name of the setting
     * @param listener
     *            the listener to be called
     */
    public void addSettingsListener(String property, SettingsListener listener) {

    }

    /**
     * Remove a SettingsListener from a setting with the given name.
     * 
     * @param property
     *            the name of the setting
     * @param listener
     *            the listener to remove
     */
    public void removeSettingsListener(String property, SettingsListener listener) {

    }

    /**
     * Stores a setting of type Integer using a given name.
     * 
     * @param property
     *            the name of the setting
     * @param value
     *            the value of the setting
     */
    public void putInteger(final String property, Integer value) {

    }

    /**
     * Stores a setting of type Double using a given name.
     * 
     * @param property
     *            the name of the setting
     * @param value
     *            the value of the setting
     */
    public void putDouble(final String property, Integer value) {

    }

    /**
     * Stores a setting of type Float using a given name.
     * 
     * @param property
     *            the name of the setting
     * @param value
     *            the value of the setting
     */
    public void putFloat(final String property, Float value) {

    }

    /**
     * Stores a setting of type Long using a given name.
     * 
     * @param property
     *            the name of the setting
     * @param value
     *            the value of the setting
     */
    public void putLong(String property, Long value) {

    }

    /**
     * TODO: Ask if property can be lost here.
     */
    public void putLocation(final String property, Location value) {

    }

    /**
     * Stores a setting of type Boolean using a given name.
     * 
     * @param property
     *            the name of the setting
     * @param value
     *            the value of the setting
     */
    public void putBoolean(final String property, Boolean value) {

    }

    /**
     * Stores a setting of type String using a given name.
     * 
     * @param property
     *            the name of the setting
     * @param value
     *            the value of the setting
     */
    public void putString(final String property, String value) {

    }

    /**
     * Retrieve the setting stored, using the given key, as Boolean.
     * 
     * @param property
     *            the name of the setting
     * @return The setting stored under the given key as Boolean or null if no
     *         setting found with given name or the setting is no instance of
     *         Boolean
     */
    public final Boolean getBoolean(final String property) {
        return null;
    }

    /**
     * Retrieve the setting stored, using the given key, as String.
     * 
     * @param property
     *            the name of the setting
     * @return The setting stored under the given key as String or null if no
     *         setting found with given name or the setting is no instance of
     *         String
     */
    public final String getString(String property) {
        return null;
    }

    /**
     * Retrieve the setting stored, using the given key, as Long.
     * 
     * @param property
     *            the name of the setting
     * @return The setting stored under the given key as Long or null if no
     *         setting found with given name or the setting is no instance of
     *         Long
     */
    public Long getLong(String property) {
        return null;
    }

    /**
     * Retrieve the setting stored, using the given key, as Float.
     * 
     * @param property
     *            the name of the setting
     * @return The setting stored under the given key as Float or null if no
     *         setting found with given name or the setting is no instance of
     *         Float
     */
    public Float getFloat(String property) {
        return null;
    }

    /**
     * Retrieve the setting stored, using the given key, as Double.
     * 
     * @param property
     *            the name of the setting
     * @return The setting stored under the given key as Double or null if no
     *         setting found with given name or the setting is no instance of
     *         Double
     */
    public Double getDouble(String property) {
        return null;
    }

    /**
     * Retrieve the setting stored, using the given key, as Integer.
     * 
     * @param property
     *            the name of the setting
     * @return The setting stored under the given key as Integer or null if no
     *         setting found with given name or the setting is no instance of
     *         Integer
     */
    public Integer getInteger(String property) {
        return null;
    }

    /**
     * TODO: Ask what he thought there, why there is a property;
     */
    public Location[] getLocations(String property) {
        return null;
    }

}
