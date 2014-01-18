package de.joglearth.map.osm;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import de.joglearth.map.TileName;
import de.joglearth.source.caching.PathTranslator;


/**
 * Implements the {@link de.joglearth.source.caching.PathTranslator} interface to work with 
 * OpenStreetMap image data.
 */
public class OSMPathTranslator implements PathTranslator<TileName> {

    @Override
    public String toFileSystemPath(TileName name) {
        if (!(name.tile instanceof OSMTile || name.tile instanceof OSMPole)
                || !(name.configuration instanceof OSMMapConfiguration)) {
            throw new IllegalArgumentException(name.tile.getClass().getCanonicalName());
        }
        String fileName;
        OSMMapConfiguration osmConfig = (OSMMapConfiguration) name.configuration;
        if (name.tile instanceof OSMTile) {
            OSMTile osmTile = (OSMTile) name.tile;
            fileName = String.format("%s-%d-%d-%d.%s", osmConfig.getMapType().toString(),
                    osmTile.getDetailLevel(), osmTile.getLongitudeIndex(),
                    osmTile.getLatitudeIndex(), osmConfig.getImageFormatSuffix());
        } else {
            OSMPole osmPole = (OSMPole) name.tile;
            fileName = String.format("%s-%d.%s", osmConfig.getMapType().toString(),
                    osmPole.getPole(), osmConfig.getImageFormatSuffix());
        }
        // Divide the set of files into (at most) 4096 sub-folders to avoid file system bottlenecks
        byte[] bytesOfMessage = null;
        try {
            bytesOfMessage = fileName.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        String hexHash = DatatypeConverter.printHexBinary(md5.digest(bytesOfMessage));

        return hexHash.substring(0, 3) + File.separator + fileName;

    }


    private final static Pattern TILEPATH_PATTERN = Pattern.compile("[0-9a-fA-F]{3}"
            + Pattern.quote(File.separator) + "([A-Z0-9_]+)-([0-9]+)-([0-9]+)-([0-9]+)\\.\\w+");
    private final static Pattern POLEPATH_PATTERN = Pattern.compile("[0-9a-fA-F]{3}"
            + Pattern.quote(File.separator) + "([A-Z0-9_]+)-([0-9]+)\\.\\w+");


    @Override
    public TileName fromFileSystemPath(String s) {
        Matcher m = TILEPATH_PATTERN.matcher(s);

        if (m.matches()) {
            OSMMapType type = OSMMapType.valueOf(m.group(1));
            int zoom = Integer.parseInt(m.group(2));
            int lonIndex = Integer.parseInt(m.group(3));
            int latIndex = Integer.parseInt(m.group(4));

            return new TileName(new OSMMapConfiguration(type),
                    new OSMTile(zoom, lonIndex, latIndex));
        } else {
            m = POLEPATH_PATTERN.matcher(s);
            if (m.matches()) {
                OSMMapType type = OSMMapType.valueOf(m.group(1));
                int poleType = Integer.parseInt(m.group(2));
                return new TileName(new OSMMapConfiguration(type), new OSMPole(0, poleType));
            }
        }

        return null;
    }

}
