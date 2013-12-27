package de.joglearth.source.tiles.osm;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import de.joglearth.geometry.Tile;
import de.joglearth.source.caching.PathTranslator;
import de.joglearth.source.tiles.TileName;


/**
 * Implements the {@link de.joglearth.source.caching.PathTranslator} interface
 * {@link de.joglearth.source.osm.OSMTileImageName}.
 */
public class OSMPathTranslator implements PathTranslator<TileName> {

    @Override
    public String toFileSystemPath(TileName name) {
        if (!(name.tile instanceof OSMTile) || !(name.configuration instanceof OSMMapConfiguration)) {
            throw new IllegalArgumentException();
        }
        
        OSMTile osmTile = (OSMTile) name.tile;
        OSMMapConfiguration osmConfig = (OSMMapConfiguration) name.configuration;
        
        String fileName = String.format("%s-%d-%d-%d.png", osmConfig.getMapType().toString(), 
                osmTile.getDetailLevel(), osmTile.getLongitudeIndex(), osmTile.getLatitudeIndex());

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
    

    private final static Pattern pathPattern = Pattern.compile("[0-9a-fA-F]{3}"
            + Pattern.quote(File.separator) + "([A-Z0-9_]+)-([0-9]+)-([0-9]+)-([0-9]+)\\.png");

    @Override
    public TileName fromFileSystemPath(String s) {
        Matcher m = pathPattern.matcher(s);

        if (m.matches()) {            
            OSMMapType type = OSMMapType.valueOf(m.group(1));
            int zoom = Integer.parseInt(m.group(2));
            int lonIndex = Integer.parseInt(m.group(3));
            int latIndex = Integer.parseInt(m.group(4));
            
            return new TileName(new OSMMapConfiguration(type), 
                    new OSMTile(zoom, lonIndex, latIndex));
        }

        return null;
    }

}
