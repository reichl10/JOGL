package de.joglearth.settings;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import de.joglearth.location.Location;


/**
 * Used to store settings of JoglEarth. A key can only have one value, except for Locations where
 * multiple can exist under the same key. If put a value of an other type under the same key it
 * replaces the old value of the other type. For key the <code>null</code> object is not allowed.
 * This class is thread-safe.
 */
public final class Settings {

    private final Map<String, Object> valueMap;
    private final Map<String, List<SettingsListener>> listenerMap;
    /**
     * Class to store settings.
     */
    private static Settings instance = null;


    /**
     * Private Constructor to prevent creating an instance. Use {@link #getInstance()} instead.
     */
    private Settings() {
        valueMap = new ConcurrentHashMap<String, Object>();
        listenerMap = new ConcurrentHashMap<String, List<SettingsListener>>();
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
     * Add a {@link de.joglearth.settings.SettingsListener} to be called if the setting with the
     * given name is changed.
     * 
     * @param key The key of the setting. <code>Null</code> is not allowed.
     * @param listener The listener to be called
     */
    public void addSettingsListener(final String key, final SettingsListener listener) {
        List<SettingsListener> l = listenerMap.get(key);
        if (l == null) {
            l = new LinkedList<SettingsListener>();
            listenerMap.put(key, l);
        }
        l.add(listener);
    }

    /**
     * Unregisters the given {@link de.joglearth.settings.SettingsListener} from being called if the
     * setting with the given name changes.
     * 
     * @param key The key of the setting. <code>Null</code> is not allowed.
     * @param listener The listener to remove
     */
    public void removeSettingsListener(final String key, final SettingsListener listener) {
        List<SettingsListener> l = listenerMap.get(key);
        if (l == null) {
            return;
        }
        while (l.remove(listener) == true);
    }

    /**
     * Stores a setting of type <code>Integer</code> using a given key.
     * 
     * @param key The key of the setting. <code>Null</code> is not allowed.
     * @param value The value of the setting
     */
    public synchronized void putInteger(final String key, final Integer value) {
        putObjectAndCallListeners(key, value);
    }

    /**
     * Stores a setting of type <code>Double</code> using a given key.
     * 
     * @param key The key of the setting. <code>Null</code> is not allowed.
     * @param value The value of the setting
     */
    public synchronized void putDouble(final String key, final Double value) {
        putObjectAndCallListeners(key, value);
    }

    /**
     * Stores a setting of type <code>Float</code> using a given key.
     * 
     * @param key The key of the setting. <code>Null</code> is not allowed.
     * @param value The value of the setting
     */
    public synchronized void putFloat(final String key, final Float value) {
        putObjectAndCallListeners(key, value);
    }

    /**
     * Stores a setting of type <code>Long</code> using a given key.
     * 
     * @param key The key of the setting. <code>Null</code> is not allowed.
     * @param value The value of the setting
     */
    public synchronized void putLong(final String key, final Long value) {
        putObjectAndCallListeners(key, value);
    }

    /**
     * Stores a {@link de.joglearth.location.Location} using a given key.
     * 
     * @param key The locations key. <code>Null</code> is not allowed.
     * @param value The location to add to this key
     */
    public synchronized void putLocation(final String key, final Location value) {
        Object val = valueMap.get(key);
        Set<Location> set = null;
        if (val == null || !(val instanceof Set<?>)) {
            val = new HashSet<Location>();
            set = (Set<Location>) val;
            valueMap.put(key, set);
        } else {
            set = (Set<Location>) val;
        }
        set.add(value);
        callListenersForKey(key, set, value);
    }

    /**
     * Removes the given {@link de.joglearth.location.Location} from the given key. The Location that
     * is removed is found by <code>this == value || this.equals(value)</code>
     * 
     * @param key The key of the {@link de.joglearth.location.Location}, which should be removed.
     *        <code>Null</code> is not allowed.
     * @param value The <code>Location</code> to remove
     */
    public synchronized void dropLocation(final String key, final Location value) {
        Object val = valueMap.get(key);
        
        
        if ((val != null) && (val instanceof Set<?>)) {
            Set<Location> set = (Set<Location>) val;
            set.remove(value);
            callListenersForKey(key, set, value);;
        }
        
    }

    /**
     * Stores a setting of type <code>Boolean</code> using a given key.
     * 
     * @param key The key of the setting. <code>Null</code> is not allowed.
     * @param value The value of the setting
     */
    public synchronized void putBoolean(final String key, final Boolean value) {
        putObjectAndCallListeners(key, value);
    }

    /**
     * Stores a setting of type <code>String</code> using a given key.
     * 
     * @param key The key of the setting. <code>Null</code> is not allowed.
     * @param value The value of the setting
     */
    public synchronized void putString(final String key, final String value) {
        putObjectAndCallListeners(key, value);
    }

    /**
     * Retrieve the setting stored, using the given key, as <code>Boolean</code> .
     * 
     * @param key The key of the setting. <code>Null</code> is not allowed.
     * @return The setting stored under the given key as <code>Boolean</code> or <code>null</code>
     *         if no setting found with given name or the setting is no instance of
     *         <code>Boolean</code>
     */
    public synchronized Boolean getBoolean(final String key) {
        Object val = valueMap.get(key);
        if (val == null)
            return null;
        if (val.getClass() != Boolean.class)
            return null;
        return (Boolean) val;
    }

    /**
     * Retrieve the setting stored, using the given key, as <code>String</code>.
     * 
     * @param key The key of the setting. <code>Null</code> is not allowed.
     * @return The setting stored under the given key as String or <code>null</code> if no setting
     *         found with given name or the setting is no instance of <code>String</code>
     */
    public synchronized String getString(final String key) {
        Object val = valueMap.get(key);
        if (val == null)
            return null;
        if (val.getClass() != String.class)
            return null;
        return (String) val;
    }

    /**
     * Retrieve the setting stored, using the given key, as <code>Long</code>.
     * 
     * @param key The key of the setting. <code>Null</code> is not allowed.
     * @return The setting stored under the given key as <code>Long</code> or <code>null</code> if
     *         no setting found with given name or the setting is no instance of <code>Long</code>
     */
    public synchronized Long getLong(final String key) {
        Object val = valueMap.get(key);
        if (val == null)
            return null;
        if (val.getClass() != Long.class)
            return null;
        return (Long) val;
    }

    /**
     * Retrieve the setting stored, using the given key, as <code>Float</code>.
     * 
     * @param key The key of the setting. <code>Null</code> is not allowed.
     * @return The setting stored under the given key as <code>Float</code> or <code>null</code> if
     *         no setting found with given name or the setting is no instance of <code>Float</code>
     */
    public synchronized Float getFloat(final String key) {
        Object val = valueMap.get(key);
        if (val == null)
            return null;
        if (val.getClass() != Float.class)
            return null;
        return (Float) val;
    }

    /**
     * Retrieve the setting stored, using the given key, as <code>Double</code>.
     * 
     * @param key The key of the setting. <code>Null</code> is not allowed.
     * @return The setting stored under the given key as <code>Double</code> or <code>null</code> if
     *         no setting found with given name or the setting is no instance of <code>Double</code>
     */
    public synchronized Double getDouble(final String key) {
        Object val = valueMap.get(key);
        if (val == null)
            return null;
        if (val.getClass() != Double.class)
            return null;
        return (Double) val;
    }

    /**
     * Retrieve the setting stored, using the given key, as <code>Integer</code> .
     * 
     * @param key The key of the setting. <code>Null</code> is not allowed.
     * @return The setting stored under the given key as <code>Integer</code> or <code>null</code>
     *         if no setting found with given name or the setting is no instance of
     *         <code>Integer</code>
     */
    public synchronized Integer getInteger(final String key) {
        Object val = valueMap.get(key);
        if (val == null)
            return null;
        if (val.getClass() != Integer.class)
            return null;
        return (Integer) val;
    }

    /**
     * Gets the {@link de.joglearth.location.Location} objects stored using the given key.
     * 
     * @param key The key to use. <code>Null</code> is not allowed.
     * @return A <code>Set</code> of <code>Location</code> objects stored under the given key or
     *         <code>null</code> if no <code>Location</code> object is found using this key
     */
    public synchronized Set<Location> getLocations(final String key) {
        Object val = valueMap.get(key);
        if (val == null || !(val instanceof Set<?>))
            return null;
        return (Set<Location>) val;
    }

    private void putObjectAndCallListeners(final String key, final Object value) {
        Object oldval;
        if (value == null) {
            oldval = valueMap.remove(key);
        } else {
            oldval = valueMap.put(key, value);
        }
        callListenersForKey(key, oldval, value);
    }

    private void callListenersForKey(String key, Object valueOld, Object valueNew) {
        List<SettingsListener> listeners = listenerMap.get(key);
        if (listeners != null)
            for (SettingsListener l : listeners) {
                l.settingsChanged(key, valueOld, valueNew);
            }
    }

}
