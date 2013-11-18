package de.joglearth.settings;

import javax.tools.JavaFileManager.Location;



/**
 * Used to store settings of JoglEarth.
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
     * Add a <code>SettingsListener</code> to be called if setting with the given name is
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
     * Remove a <code>SettingsListener</code> from a setting with the given name.
     * 
     * @param property
     *            the name of the setting
     * @param listener
     *            the listener to remove
     */
    public void removeSettingsListener(String property, SettingsListener listener) {

    }

    /**
     * Stores a setting of type <code>Integer</code> using a given name.
     * 
     * @param property
     *            the name of the setting
     * @param value
     *            the value of the setting
     */
    public void putInteger(final String property, Integer value) {

    }

    /**
     * Stores a setting of type <code>Double</code> using a given name.
     * 
     * @param property
     *            the name of the setting
     * @param value
     *            the value of the setting
     */
    public void putDouble(final String property, Integer value) {

    }

    /**
     * Stores a setting of type <code>Float</code> using a given name.
     * 
     * @param property
     *            the name of the setting
     * @param value
     *            the value of the setting
     */
    public void putFloat(final String property, Float value) {

    }

    /**
     * Stores a setting of type <code>Long</code> using a given name.
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
     * Stores a setting of type <code>Boolean</code> using a given name.
     * 
     * @param property
     *            the name of the setting
     * @param value
     *            the value of the setting
     */
    public void putBoolean(final String property, Boolean value) {

    }

    /**
     * Stores a setting of type <code>String</code> using a given name.
     * 
     * @param property
     *            the name of the setting
     * @param value
     *            the value of the setting
     */
    public void putString(final String property, String value) {

    }

    /**
     * Retrieve the setting stored, using the given key, as <code>Boolean</code>.
     * 
     * @param property
     *            the name of the setting
     * @return The setting stored under the given key as <code>Boolean</code> or <code>null</code> if no
     *         setting found with given name or the setting is no instance of
     *         <code>Boolean</code>
     */
    public final Boolean getBoolean(final String property) {
        return null;
    }

    /**
     * Retrieve the setting stored, using the given key, as <code>String</code>.
     * 
     * @param property
     *            the name of the setting
     * @return The setting stored under the given key as String or <code>null</code> if no
     *         setting found with given name or the setting is no instance of
     *         <code>String</code>
     */
    public final String getString(String property) {
        return null;
    }

    /**
     * Retrieve the setting stored, using the given key, as <code>Long</code>.
     * 
     * @param property
     *            the name of the setting
     * @return The setting stored under the given key as <code>Long</code> or <code>null</code> if no
     *         setting found with given name or the setting is no instance of
     *         <code>Long</code>
     */
    public Long getLong(String property) {
        return null;
    }

    /**
     * Retrieve the setting stored, using the given key, as <code>Float</code>.
     * 
     * @param property
     *            the name of the setting
     * @return The setting stored under the given key as <code>Float</code> or <code>null</code> if no
     *         setting found with given name or the setting is no instance of
     *         <code>Float</code>
     */
    public Float getFloat(String property) {
        return null;
    }

    /**
     * Retrieve the setting stored, using the given key, as <code>Double</code>.
     * 
     * @param property
     *            the name of the setting
     * @return The setting stored under the given key as <code>Double</code> or <code>null</code> if no
     *         setting found with given name or the setting is no instance of
     *         <code>Double</code>
     */
    public Double getDouble(String property) {
        return null;
    }

    /**
     * Retrieve the setting stored, using the given key, as <code>Integer</code>.
     * 
     * @param property
     *            the name of the setting
     * @return The setting stored under the given key as <code>Integer</code> or <code>null</code> if no
     *         setting found with given name or the setting is no instance of
     *         <code>Integer</code>
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
