package de.joglearth.settings;

import javax.tools.JavaFileManager.Location;


public class Settings {	
	private static Settings instance = null;
	
	private String path;	
    		
    public static Settings getInstance() {
		if (instance == null)
			instance = new Settings();
		return instance;
    }

    public void addSettingsListener(String property, SettingsListener listener) {

    }

    public void removeSettingsListener(String property, SettingsListener listener) {

    }

    public void putInteger(String property, int value) {
    	
    }

    public void putDouble(String property, double value) {
    	
    }

    public void putFloat(String property, float value) {

    }

    public void putLong(String property, long value) {

    }

    public void putLocation(String property, Location value) {

    }

    public void putBoolean(String property, boolean value) {
    	
    }
    
    public void putString(String property, String value) {

    }
    
    public boolean getBoolean(String property) {
    	return false;
    }

    public String getString(String property) {
    	return null;
    }

    public long getLong() {
    	return 0L;
    }

    public float getFloat(String property) {
    	return 0;
    }

    public double getDouble(String property) {
    	return 0;
    }


    public int getInteger(String property) {
    	return 0;
    }

    public Location[] getLocations(String property) {
    	return null;
    }
    
}
