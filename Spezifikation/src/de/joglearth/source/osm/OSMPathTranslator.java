package de.joglearth.source.osm;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import de.joglearth.geometry.Tile;
import de.joglearth.rendering.LevelOfDetail;
import de.joglearth.settings.Settings;
import de.joglearth.settings.SettingsContract;
import de.joglearth.source.caching.PathTranslator;
import de.joglearth.surface.TiledMapType;


/**
 * Implements the {@link de.joglearth.source.caching.PathTranslator} interface
 * {@link de.joglearth.source.osm.OSMTile}.
 */
public class OSMPathTranslator implements PathTranslator<OSMTile> {

    @Override
    public String toFileSystemPath(OSMTile k) {
        int y = (int) (((k.tile.getLatitudeFrom() + k.tile.getLatitudeTo()) / 2) / 180 * Math.PI);
        int x = (int) ((k.tile.getLongitudeFrom() + k.tile.getLongitudeTo()) / 2);

        int zoom = k.tile.getDetailLevel();

        int n = (int) Math.pow(2, zoom);
        int xtile = n * ((x + 180) / 360);
        int ytile = (int) (n * (1 - (Math.log(Math.tan(y) + 1 / Math.cos(y)) / Math.PI)) / 2);

        StringBuilder builder = new StringBuilder();

        builder.append(k.type.toString());
        builder.append("-");
        builder.append(zoom);
        builder.append("-");
        builder.append(xtile);
        builder.append("-");
        builder.append(ytile);
        builder.append(".png");

        String fileName = builder.toString();

        byte[] bytesOfMessage = null;
        try {
            bytesOfMessage = fileName.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }

        byte[] md5Hash = null;
        if (md != null && bytesOfMessage != null) {
            md5Hash = md.digest(bytesOfMessage);
        } else {
            md5Hash = new byte[0];
        }

        String hexHash = DatatypeConverter.printHexBinary(md5Hash);

        String path = hexHash.substring(0, 3);

        return path + "/" + fileName;

    }

    @Override
    public OSMTile fromFileSystemPath(String s) {
        // TODO REGEXP so richtig?!
        Pattern p = Pattern.compile("[0-9a-fA-F]{3}/([A-Z]+)-([0-9]+)-([0-9]+)-([0-9]+).png");
        Matcher m = p.matcher(s);

        if (m.matches()) {
            int xtile = Integer.parseInt(m.group(3));
            int ytile = Integer.parseInt(m.group(4));
            int zoom = Integer.parseInt(m.group(2));
            TiledMapType type = Enum.valueOf(TiledMapType.class, m.group(1));

            // Berechnung von Längen- und Breitengrad aus den OSM-Koordinaten
            // n = 2 ^ zoom
            // lon_deg = xtile / n * 360.0 - 180.0
            // lat_rad = arctan(sinh(π * (1 - 2 * ytile / n)))
            // lat_deg = lat_rad * 180.0 / π

            int n = (int) Math.pow(2, zoom);
            int lon = (int) (xtile / n * 360.0 - 180.0);
            int lat_rad = (int) Math.atan(Math.sinh(Math.PI * (1 - 2 * ytile / n)));
            int lat = (int) (lat_rad * 180.0 / Math.PI);

            int lonIndex = (int) (lon / (2 * Math.PI / Math.pow(2, zoom)));
            int latIndex = (int) (lat / (Math.PI / Math.pow(2, zoom)));

            Tile tile = new Tile(zoom, lonIndex, latIndex);

            return new OSMTile(tile, type);

        }

        return null;
    }

    public static void main(String[] args) {
        OSMPathTranslator test = new OSMPathTranslator();
        OSMTile k = new OSMTile(new Tile(3, 2, 3), TiledMapType.CYCLING);
        System.out.println(test.toFileSystemPath(k));
        // System.out.println(test.fromFileSystemPath("d81/CYCLING-4-8-3"));
    }

}
