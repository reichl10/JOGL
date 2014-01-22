package de.joglearth.height.srtm;

import static java.lang.Math.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Structure identifying single SRTM height data tiles.
 */
public final class SRTMTileName {

    /**
     * The longitude index, as described by the SRTM standard.
     */
    public int longitude;

    /**
     * The latitude index, as described by the SRTM standard.
     */
    public int latitude;


    /**
     * Constructor.
     * 
     * @param lon The longitude index
     * @param lat The latitude index
     */
    public SRTMTileName(int lon, int lat) {
        longitude = lon;
        latitude = lat;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + latitude;
        result = prime * result + longitude;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SRTMTileName other = (SRTMTileName) obj;
        return this.longitude == other.longitude && this.latitude == other.latitude;
    }

    @Override
    public String toString() {
        return String.format("%c%02d%c%03d", latitude >= 0 ? 'N' : 'S', abs(latitude),
                longitude >= 0 ? 'E' : 'W', abs(longitude));
    }

    private static final Pattern srtmStringPattern 
        = Pattern.compile("([NS])([0-9]{2})([EW])([0-9]{3})");
    
    /**
     * TODO
     * @param s
     * @return
     */
    public static SRTMTileName parseTileIndex(String s) {
        Matcher m = srtmStringPattern.matcher(s);
        
        if(m.matches()) {
            int lat = Integer.parseInt(m.group(2));
            if(m.group(1).charAt(0) == 'S') {
                lat *= -1;
            }

            int lon = Integer.parseInt(m.group(4));
            if(m.group(3).charAt(0) == 'W') {
                lon *= -1;
            }
            
            return new SRTMTileName(lon, lat);
        } else {
            throw new NumberFormatException();
        }
    }
}
