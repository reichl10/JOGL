package de.joglearth.settings;

/**
 * Class that contains Constants and static methods to work with the
 * {@link de.joglearth.settings.Settings Settings} class.
 */
public final class SettingsContract {

    /**
     * Name constant for the Language setting.
     * You should save a string to settings using this.
     */
    public static final String LANGUAGE         = "Language";

    /**
     * Name constant for the texture filter setting.
     * You should save a boolean to settings using this. (Only on/off)
     */
    public static final String TEXTURE_FILTER   = "TextureFilter";

    /**
     * Name constant for the level of details setting.
     * You should save a String to settings using this.
     * Use <code>toString</code> of the Enum.
     */
    public static final String LEVEL_OF_DETAILS = "LevelOfDetail";

    /**
     * Name constant for the users Locations.
     * You should save {@link Location} objects using this key.
     */
    public static final String USER_LOCATIONS = "UserLocations";
    
    /**
     * Name constant for Antialiasing.
     * You should save a boolean using this key. (Only on/off)
     */
    public static final String ANTIALIASING = "Antialiasing";
    
    /**
     * Name constant for the memory cache's size.
     * You should save an integer using this key.
     */
    public static final String CACHE_SIZE_MEMORY = "CacheSizeMemory";
    
    /**
     * Name constant for the file system cache's size.
     * You should save an integer using this key.
     */
    public static final String CACHE_SIZE_FILESYSTEM = "CacheSizeFileSystem";

    /**
     * Private constructor to prevent creating instances of this class.
     */
    private SettingsContract() {}

    /**
     * Inserts the default values for each of the settings defined in this contract.
     */
    public static void setDefaultSettings() {

    }

    /**
     * Loads the values for the settings defined in this contract from a file. This loads from the
     * same files the {@link #saveSettings()} saves to.
     */
    public static void loadSettings() {

    }

    /**
     * Saves the settings defined in this contract to a file. This saves to the same files the
     * {@link #loadSettings()} loads them from.
     */
    public static void saveSettings() {

    }

}
