package de.joglearth.settings;


public interface SettingsListener {
    void settingsChanged(String key, Object valOld, Object valNew);
}
