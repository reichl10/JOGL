package de.joglearth.settings;

/**
 * Class that contains Constants and static methods to work with the
 * {@link de.joglearth.settings.Settings Settings} class.
 */
public final class SettingsContract {

    /**
     * Name Constant for the Language setting.
     */
    public static final String LANGUAGE         = "lang";

    /**
     * Name Constant for the texture filter setting.
     */
    public static final String TEXTURE_FILTER   = "textureFilter";

    /**
     * Name Constant for the Level of Details setting.
     */
    public static final String LEVEL_OF_DETAILS = "lvlOfDetails";


    // TODO: Define others

    /**
     * Private Constructor to prevent creating instances of this class.
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
