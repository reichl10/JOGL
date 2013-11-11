package de.joglearth.settings;


public interface SettingsListener {
    void onChange(String key, Object valOld, Object valNew);
}
