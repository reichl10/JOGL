package de.joglearth.settings;

import de.joglearth.rendering.AntialiasingType;
import de.joglearth.rendering.LevelOfDetail;
import de.joglearth.surface.Location;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

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
     * Use <code>name</code> of the Enum.
     */
    public static final String LEVEL_OF_DETAILS = "LevelOfDetail";

    /**
     * Name constant for the users Locations.
     * You should save {@link de.joglearth.surface.Location} objects using this key.
     */
    public static final String USER_LOCATIONS = "UserLocations";
    
    /**
     * Name constant for Antialiasing.
     * You should save a String of AntialiasingType.name using this key.
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
    private static final String XML_ELEMENT_ROOT = "settings";
    private static final String XML_ATTR_TYPE_BOOLEAN = "Boolean";
    private static final String XML_ATTR_TYPE_DOUBLE = "Double";
    private static final String XML_ATTR_TYPE_STRING = "String";
    private static final String XML_ATTR_TYPE_INTEGER = "Integer";
    private static final String XML_ATTR_TYPE_FLOAT = "Float";
    private static final String XML_ELEMENT_ENTRY = "entry";
    private static final String XML_ATTR_KEY = "key";
    private static final String XML_ATTR_TYPE = "type";
    private static final String XML_ATTR_VALUE = "value";
    private static final String XML_ELEMENT_LOCS = "locations";
    private static final String XML_ATTR_LOCS_KEY = "key";
    private static final String XML_ELEMENT_LOC = "location";
    private static final String XML_ATTR_LOC_DETAILS = "details";
    private static final String XML_ATTR_LOC_NAME = "name";
    private static final String XML_ATTR_LOC_TYPE = "type";
    private static final String XML_ELEMENT_GEO = "GeoCoordinates";
    private static final String XML_ATTR_LONG = "longitude";
    private static final String XML_ATTR_LAT = "latitude";

    /**
     * Private constructor to prevent creating instances of this class.
     */
    private SettingsContract() {}

    /**
     * Inserts the default values for each of the settings defined in this contract.
     */
    public static void setDefaultSettings() {
        Settings s = Settings.getInstance();
        s.putString(LANGUAGE, "DE");
        s.putBoolean(TEXTURE_FILTER, false);
        s.putString(ANTIALIASING, AntialiasingType.MSAA_4.name());
        s.putString(LEVEL_OF_DETAILS, LevelOfDetail.MEDIUM.name());
        s.putInteger(CACHE_SIZE_FILESYSTEM, new Integer(1000));
        s.putInteger(CACHE_SIZE_MEMORY, new Integer(200));
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
        Settings s = Settings.getInstance();
        XMLStreamWriter xmlWriter = null;
        try {
            xmlWriter = XMLOutputFactory.newInstance()
                .createXMLStreamWriter(new FileOutputStream(""), "UTF-8");
            writeStart(xmlWriter);
            writeEntry(xmlWriter, LANGUAGE, s.getString(LANGUAGE));
            writeEntry(xmlWriter, TEXTURE_FILTER, s.getBoolean(TEXTURE_FILTER));
            writeEntry(xmlWriter, LEVEL_OF_DETAILS, s.getString(LEVEL_OF_DETAILS));
            writeEntry(xmlWriter, ANTIALIASING, s.getString(ANTIALIASING));
            writeEntry(xmlWriter, CACHE_SIZE_FILESYSTEM, s.getInteger(CACHE_SIZE_FILESYSTEM));
            writeEntry(xmlWriter, CACHE_SIZE_MEMORY, s.getInteger(CACHE_SIZE_MEMORY));
            /**Field[] fields = Settings.class.getDeclaredFields();
             for (Field f : fields) {
             int mod = f.getModifiers();
             if (Modifier.isPrivate(mod) && Modifier.isFinal(mod) && Modifier.isStatic(mod)) {
             // TODO: Write to as Entry.
             }
             } **/
            writeLocationSet(xmlWriter, USER_LOCATIONS, s.getLocations(USER_LOCATIONS));
            writeEnd(xmlWriter);
            xmlWriter.close();
        } catch (FileNotFoundException fex) {
           return;
        } catch (XMLStreamException xex) {
            return;
        }
    }

    private static void writeStart(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartDocument();
        writer.writeStartElement(XML_ELEMENT_ROOT);// START ROOT
    }
    private static void writeEnd(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEndElement();// END ROOT
        writer.writeEndDocument();
    }
    private static void writeEntry(XMLStreamWriter writer, String key, Object value) throws XMLStreamException {
        String valueS = "";
        String type = "";
        writer.writeStartElement(XML_ELEMENT_ENTRY);
        writer.writeAttribute(XML_ATTR_KEY, key);
        if (value instanceof Integer) {
            type = XML_ATTR_TYPE_INTEGER;
            valueS = String.valueOf((Integer) value);
        } else if (value instanceof String) {
            type = XML_ATTR_TYPE_STRING;
            valueS = (String) value;
        } else if (value instanceof Double) {
            type = XML_ATTR_TYPE_DOUBLE;
            valueS = String.valueOf((Double) value);
        } else if (value instanceof Boolean) {
            type = XML_ATTR_TYPE_BOOLEAN;
            valueS = String.valueOf((Boolean) value);
        } else if (value instanceof Float) {
            type = XML_ATTR_TYPE_FLOAT;
            valueS = String.valueOf((Float) value);
        } else {
            // TODO: Error out :P
        }
        writer.writeAttribute(XML_ATTR_TYPE, type);
        writer.writeAttribute(XML_ATTR_VALUE, valueS);
        writer.writeEndElement(); // END ENTRY
    }
    private static void writeLocationSet(XMLStreamWriter writer, String key, Set<Location> set) throws XMLStreamException {
        writer.writeStartElement(XML_ELEMENT_LOCS);
        writer.writeAttribute(XML_ATTR_LOCS_KEY, key);
        for(Location l : set)
             writeLocation(writer, l);
        writer.writeEndElement()
    }

    private static void writeLocation(XMLStreamWriter writer, Location l) {
        // TODO: Implement this shit
    }

    private static void writeGeoCoordinate(XMLStreamWriter writer, GeoCoordinates geo) {
        // TODO: Implement this shit
    }
}
