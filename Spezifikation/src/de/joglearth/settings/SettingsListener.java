package de.joglearth.settings;

/**
 * The listener interface to receive notification about a changed setting. Classes interested in
 * listening to settings changes implement this interface. The listener object created from that
 * class can then be registered on a {@link Settings} object for a setting
 * using {@link Settings.addSettingsListener(String key, SettingsListener listener)}.
 */
public interface SettingsListener {

    /**
     * Invoked if a setting this listener has be registered on is changed.
     * 
     * @param key The key of the changed setting
     * @param valOld The old value of the setting, can be <code>null</code> if there wasn't any
     * @param valNew The new value of the setting, can be <code>null</code>
     */
    void settingsChanged(String key, Object valOld, Object valNew);
}
