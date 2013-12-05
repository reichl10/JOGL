package de.joglearth.settings;

import static javax.xml.stream.XMLStreamConstants.CHARACTERS;
import static javax.xml.stream.XMLStreamConstants.END_DOCUMENT;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Set;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.rendering.AntialiasingType;
import de.joglearth.rendering.LevelOfDetail;
import de.joglearth.surface.Location;
import de.joglearth.surface.LocationType;


/**
 * Class that contains Constants and static methods to work with the
 * {@link de.joglearth.settings.Settings Settings} class.
 */
public final class SettingsContract {

    private static final String XML_ENCODING = "UTF-8";

    /**
     * Name constant for the Language setting. You should save a string to settings using this.
     */
    public static final String LANGUAGE = "Language";

    /**
     * Name constant for the texture filter setting. You should save a boolean to settings using
     * this. (Only on/off)
     */
    public static final String TEXTURE_FILTER = "TextureFilter";

    /**
     * Name constant for the level of details setting. You should save a String to settings using
     * this. Use <code>name</code> of the Enum.
     */
    public static final String LEVEL_OF_DETAILS = "LevelOfDetail";

    /**
     * Name constant for the users Locations. You should save {@link de.joglearth.surface.Location}
     * objects using this key.
     */
    public static final String USER_LOCATIONS = "UserLocations";

    /**
     * Name constant for Antialiasing. You should save a String of AntialiasingType.name using this
     * key.
     */
    public static final String ANTIALIASING = "Antialiasing";

    /**
     * Name constant for the memory cache's size. You should save an integer using this key.
     */
    public static final String CACHE_SIZE_MEMORY = "CacheSizeMemory";

    /**
     * Name constant for the file system cache's size. You should save an integer using this key.
     */
    public static final String CACHE_SIZE_FILESYSTEM = "CacheSizeFileSystem";
    private static final String XML_ELEMENT_SETTINGS = "settings";
    private static final String XML_ATTR_TYPE_BOOLEAN = "Boolean";
    private static final String XML_ATTR_TYPE_DOUBLE = "Double";
    private static final String XML_ATTR_TYPE_STRING = "String";
    private static final String XML_ATTR_TYPE_INTEGER = "Integer";
    private static final String XML_ATTR_TYPE_LONG = "Long";
    private static final String XML_ATTR_TYPE_FLOAT = "Float";
    private static final String XML_ELEMENT_ENTRY = "entry";
    private static final String XML_ATTR_ENTRY_KEY = "key";
    private static final String XML_ATTR_ENTRY_TYPE = "type";
    private static final String XML_ATTR_ENTRY_VALUE = "value";
    private static final String XML_ELEMENT_LOCS = "locations";
    private static final String XML_ATTR_LOCS_KEY = "key";
    private static final String XML_ELEMENT_LOC = "location";
    private static final String XML_ATTR_LOC_DETAILS = "details";
    private static final String XML_ATTR_LOC_NAME = "name";
    private static final String XML_ATTR_LOC_TYPE = "type";
    private static final String XML_ELEMENT_GEO = "GeoCoordinates";
    private static final String XML_ATTR_GEO_LONG = "longitude";
    private static final String XML_ATTR_GEO_LAT = "latitude";
    private static final String FILE_LOCATION = "test.xml";


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
        Settings settings = Settings.getInstance();
        try {
            XMLStreamReader xmlReader = XMLInputFactory.newInstance().createXMLStreamReader(
                    new FileInputStream(FILE_LOCATION), XML_ENCODING);
            while (xmlReader.hasNext()) {
                int event = xmlReader.next();
                switch (event) {
                    case END_DOCUMENT:
                        xmlReader.close();
                        break;
                    case START_ELEMENT:
                        if (xmlReader.getLocalName().equals(XML_ELEMENT_SETTINGS)) {
                            readSettings(xmlReader);
                            xmlReader.require(END_ELEMENT, null,
                                    XML_ELEMENT_SETTINGS);
                        }
                    case END_ELEMENT:
                    case CHARACTERS:
                        break;

                    default:// We don't need the other stuff
                        break;
                }
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (XMLStreamException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FactoryConfigurationError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static boolean readSettings(XMLStreamReader reader) throws XMLStreamException {
        reader.require(XMLStreamConstants.START_ELEMENT, null, XML_ELEMENT_SETTINGS);
        while (reader.hasNext()) {
            int event = reader.next();
            switch (event) {
                case END_DOCUMENT:
                    return false;
                case START_ELEMENT:
                    if (reader.getLocalName().equals(XML_ELEMENT_ENTRY)) {
                        readEntry(reader);
                        reader.require(END_ELEMENT, null, XML_ELEMENT_ENTRY);
                    } else if (reader.getLocalName().equals(XML_ELEMENT_LOCS)) {
                        readLocations(reader);
                        reader.require(END_ELEMENT, null, XML_ELEMENT_LOCS);
                    }
                case END_ELEMENT:
                    if (reader.getLocalName().equals(XML_ELEMENT_SETTINGS)) {
                        return true;
                    }
                default:
                    break;
            }
        }
        return true;
    }

    private static void readEntry(XMLStreamReader reader) throws XMLStreamException {
        reader.require(START_ELEMENT, null, XML_ELEMENT_ENTRY);
        Settings settings = Settings.getInstance();
        String keyString = reader.getAttributeValue(null, XML_ATTR_ENTRY_KEY);
        String typeString = reader.getAttributeValue(null, XML_ATTR_ENTRY_TYPE);
        String valueString = reader.getAttributeValue(null, XML_ATTR_ENTRY_VALUE);
        // TODO: convert and add to settings
        if (keyString != null && typeString != null && valueString != null) {
            Object value = null;
            if (typeString.equals(XML_ATTR_TYPE_INTEGER)) {
                value = Integer.valueOf(valueString);
                settings.putInteger(keyString, (Integer) value);
            } else if (typeString.equals(XML_ATTR_TYPE_STRING)) {
                value = valueString;
                settings.putString(keyString, (String) value);
            } else if (typeString.equals(XML_ATTR_TYPE_DOUBLE)) {
                value = Double.valueOf(valueString);
                settings.putDouble(keyString, (Double) value);
            } else if (typeString.equals(XML_ATTR_TYPE_BOOLEAN)) {
                value = Boolean.valueOf(valueString);
                settings.putBoolean(keyString, (Boolean) value);
            } else if (typeString.equals(XML_ATTR_TYPE_FLOAT)) {
                value = Float.valueOf(valueString);
                settings.putFloat(keyString, (Float) value);
            } else if (typeString.equals(XML_ATTR_TYPE_LONG)) {
                value = Long.valueOf(valueString);
                settings.putLong(keyString, (Long) value);
            } else {
                value = null;
            }
        }
        while (reader.hasNext() && reader.next() != END_ELEMENT
                && !reader.getLocalName().equals(XML_ELEMENT_ENTRY))
            ;
    }

    private static void readLocations(XMLStreamReader reader) throws XMLStreamException {
        reader.require(START_ELEMENT, null, XML_ELEMENT_LOCS);
        Settings settings = Settings.getInstance();
        String keyString = reader.getAttributeValue(null, XML_ATTR_LOCS_KEY);
        endloop: while (reader.hasNext()) {
            int event = reader.next();
            switch (event) {
                case START_ELEMENT:
                    // Allowed: Location
                    if (reader.getLocalName().equals(XML_ELEMENT_LOC)) {
                        Location location = readLocation(reader);
                        settings.putLocation(keyString, location);
                        reader.require(END_ELEMENT, null, XML_ELEMENT_LOC);
                    }
                    break;
                case END_ELEMENT:
                    if (reader.getLocalName().equals(XML_ELEMENT_LOCS)) {
                        break endloop;
                    }
                    break;
                default:
                    break;// ignore other things that don't belong here
            }
        }
    }

    private static Location readLocation(XMLStreamReader reader) throws XMLStreamException {
        reader.require(START_ELEMENT, null, XML_ELEMENT_LOC);
        GeoCoordinates geoCoordinates = null;
        String typeString = reader.getAttributeValue(null, XML_ATTR_LOC_TYPE);
        String detailsString = reader.getAttributeValue(null, XML_ATTR_LOC_DETAILS);
        String nameString = reader.getAttributeValue(null, XML_ATTR_LOC_NAME);
        endloop: while (reader.hasNext()) {
            int event = reader.next();
            switch (event) {
                case START_ELEMENT:
                    if (reader.getLocalName().equals(XML_ELEMENT_GEO)) {
                        geoCoordinates = readGeo(reader);
                        reader.require(END_ELEMENT, null, XML_ELEMENT_GEO);
                    }
                    break;
                case END_ELEMENT:
                    if (reader.getLocalName().equals(XML_ELEMENT_LOC)) {
                        break endloop;
                    }
                    break;
                default:
                    break;
            }
        }
        if (geoCoordinates != null && typeString != null && detailsString != null
                && nameString != null) {
            try {
                LocationType type = Enum.valueOf(LocationType.class, typeString);
                Location l = new Location(geoCoordinates, type, detailsString, nameString);
                return l;
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }

    private static GeoCoordinates readGeo(XMLStreamReader reader) throws XMLStreamException {
        reader.require(START_ELEMENT, null, XML_ELEMENT_GEO);
        String latString = reader.getAttributeValue(null, XML_ATTR_GEO_LAT);
        String longString = reader.getAttributeValue(null, XML_ATTR_GEO_LONG);
        // go to end of element
        while (reader.hasNext() && reader.next() != END_ELEMENT
                && reader.getLocalName().equals(XML_ELEMENT_GEO)) {

        }
        if (latString == null || longString == null) {
            return null;
        }
        Double lon = null;
        Double lat = null;
        try {
            lon = Double.valueOf(longString);
            lat = Double.valueOf(latString);
        } catch (NumberFormatException e) {
            return null;
        }
        return new GeoCoordinates(lon, lat);
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
                    .createXMLStreamWriter(new FileOutputStream(FILE_LOCATION), XML_ENCODING);
            writeStart(xmlWriter);
            writeEntry(xmlWriter, LANGUAGE, s.getString(LANGUAGE));
            writeEntry(xmlWriter, TEXTURE_FILTER, s.getBoolean(TEXTURE_FILTER));
            writeEntry(xmlWriter, LEVEL_OF_DETAILS, s.getString(LEVEL_OF_DETAILS));
            writeEntry(xmlWriter, ANTIALIASING, s.getString(ANTIALIASING));
            writeEntry(xmlWriter, CACHE_SIZE_FILESYSTEM, s.getInteger(CACHE_SIZE_FILESYSTEM));
            writeEntry(xmlWriter, CACHE_SIZE_MEMORY, s.getInteger(CACHE_SIZE_MEMORY));
            /**
             * Field[] fields = Settings.class.getDeclaredFields(); for (Field f : fields) { int mod
             * = f.getModifiers(); if (Modifier.isPrivate(mod) && Modifier.isFinal(mod) &&
             * Modifier.isStatic(mod)) { // TODO: Write to as Entry. } }
             **/
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
        writer.writeStartElement(XML_ELEMENT_SETTINGS);// START ROOT
    }

    private static void writeEnd(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEndElement();// END ROOT
        writer.writeEndDocument();
    }

    private static void writeEntry(XMLStreamWriter writer, String key, Object value)
            throws XMLStreamException {
        String valueS = "";
        String type = "";
        writer.writeStartElement(XML_ELEMENT_ENTRY);
        writer.writeAttribute(XML_ATTR_ENTRY_KEY, key);
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
        writer.writeAttribute(XML_ATTR_ENTRY_TYPE, type);
        writer.writeAttribute(XML_ATTR_ENTRY_VALUE, valueS);
        writer.writeEndElement(); // END ENTRY
    }

    private static void writeLocationSet(XMLStreamWriter writer, String key, Set<Location> set)
            throws XMLStreamException {
        writer.writeStartElement(XML_ELEMENT_LOCS);
        writer.writeAttribute(XML_ATTR_LOCS_KEY, key);
        for (Location l : set)
            writeLocation(writer, l);
        writer.writeEndElement();
    }

    private static void writeLocation(XMLStreamWriter writer, Location l) throws XMLStreamException {
        writer.writeStartElement(XML_ELEMENT_LOC);
        writer.writeAttribute(XML_ATTR_LOC_DETAILS, l.details);
        writer.writeAttribute(XML_ATTR_LOC_NAME, l.name);
        writer.writeAttribute(XML_ATTR_LOC_TYPE, l.type.name());
        writeGeoCoordinate(writer, l.point);
        writer.writeEndElement();
    }

    private static void writeGeoCoordinate(XMLStreamWriter writer, GeoCoordinates geo)
            throws XMLStreamException {
        writer.writeStartElement(XML_ELEMENT_GEO);
        writer.writeAttribute(XML_ATTR_GEO_LONG, new Double(geo.getLongitude()).toString());
        writer.writeAttribute(XML_ATTR_GEO_LAT, new Double(geo.getLatitude()).toString());
        writer.writeEndElement();
    }
}
