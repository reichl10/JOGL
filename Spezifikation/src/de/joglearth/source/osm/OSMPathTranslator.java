package de.joglearth.source.osm;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import de.joglearth.geometry.Tile;
import de.joglearth.source.caching.PathTranslator;
import de.joglearth.surface.TiledMapType;


/**
 * Implements the {@link de.joglearth.source.caching.PathTranslator} interface
 * {@link de.joglearth.source.osm.OSMTile}.
 */
public class OSMPathTranslator implements PathTranslator<OSMTile> {

    @Override
    public String toFileSystemPath(OSMTile k) {        
        String fileName = String.format("%s-%d-%d-%d.png", k.type.toString(), 
                k.tile.getDetailLevel(), k.tile.getLongitudeIndex(), k.tile.getLatitudeIndex());

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
    public OSMTile fromFileSystemPath(String s) {
        Matcher m = pathPattern.matcher(s);

        if (m.matches()) {            
            TiledMapType type = TiledMapType.valueOf(m.group(1));
            int zoom = Integer.parseInt(m.group(2));
            int lonIndex = Integer.parseInt(m.group(3));
            int latIndex = Integer.parseInt(m.group(4));
            
            return new OSMTile(new Tile(zoom, lonIndex, latIndex), type);
        }

        return null;
    }

}
