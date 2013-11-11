package de.joglearth.settings;


public class Settings {
	
	private static Settings instance = null;

    private Settings(String path) {}
    
    public static Settings getInstance() {
		if (instance == null)
			instance = new Settings("");
		return instance;
    }

    public void putInteger() {
        // begin-user-code
        // TODO Automatisch erstellter Methoden-Stub

        // end-user-code
    }

    public void save() {
        // begin-user-code
        // TODO Automatisch erstellter Methoden-Stub

        // end-user-code
    }

    public void load() {
        // begin-user-code
        // TODO Automatisch erstellter Methoden-Stub

        // end-user-code
    }

    public void addListener() {
        // begin-user-code
        // TODO Automatisch erstellter Methoden-Stub

        // end-user-code
    }

    public void removeListener() {
        // begin-user-code
        // TODO Automatisch erstellter Methoden-Stub

        // end-user-code
    }

    public void putDouble() {
        // begin-user-code
        // TODO Automatisch erstellter Methoden-Stub

        // end-user-code
    }

    public void putFloat() {
        // begin-user-code
        // TODO Automatisch erstellter Methoden-Stub

        // end-user-code
    }

    public void getBoolean() {
        // begin-user-code
        // TODO Automatisch erstellter Methoden-Stub

        // end-user-code
    }

    public void putLong() {
        // begin-user-code
        // TODO Automatisch erstellter Methoden-Stub

        // end-user-code
    }

    public void putPlace() {
        // begin-user-code
        // TODO Automatisch erstellter Methoden-Stub

        // end-user-code
    }

    public void getString() {
        // begin-user-code
        // TODO Automatisch erstellter Methoden-Stub

        // end-user-code
    }

    public void getLong() {
        // begin-user-code
        // TODO Automatisch erstellter Methoden-Stub

        // end-user-code
    }

    public void putBoolean() {
        // begin-user-code
        // TODO Automatisch erstellter Methoden-Stub

        // end-user-code
    }

    public void getFloat() {
        // begin-user-code
        // TODO Automatisch erstellter Methoden-Stub

        // end-user-code
    }

    public void getDouble() {
        // begin-user-code
        // TODO Automatisch erstellter Methoden-Stub

        // end-user-code
    }

    public void putString() {
        // begin-user-code
        // TODO Automatisch erstellter Methoden-Stub

        // end-user-code
    }

    public void getInteger() {
        // begin-user-code
        // TODO Automatisch erstellter Methoden-Stub

        // end-user-code
    }

    public void getPlaces() {

    }

    public void addSettingsListener(String key, SettingsListener listener) {}

}
