package de.joglearth.source.tiles.osm;

import static java.lang.Math.*;
import de.joglearth.geometry.AbstractTile;
import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.GridPoint;
import de.joglearth.geometry.Tile;


/**
 * A structure identifying a single OpenStreetMap surface tile.
 */
public final class OSMTile extends AbstractTile {

    private int detailLevel;
    private int lonIndex;
    private int latIndex;
    
    
    public static final double MAX_LATITUDE = atan(sinh(PI));
    
    public static final double MIN_LATITUDE = -MAX_LATITUDE;


    /**
     * Constructor.
     * 
     * @param detailLevel How often the globe is subdivided to reach the desired tile size
     * @param lonIndex The number of tiles to skip, starting from the north pole, to reach the
     *        desired longitude
     * @param latIndex The number of tiles to skip, starting from latitude 0, to reach the left
     *        bound of the tile
     */
    public OSMTile(int detailLevel, int lonIndex, int latIndex) {
        this.detailLevel = detailLevel;
        int maxIndex = (int) pow(2, detailLevel);
        
        if (lonIndex >= maxIndex || lonIndex < 0 || latIndex >= maxIndex || lonIndex < 0) {
            throw new IllegalArgumentException();
        }
        this.lonIndex = lonIndex;
        this.latIndex = latIndex;
    }

    // Returns the angle for the step given, in radians
    private double getLongitudeAngle(int steps) {
        if (detailLevel > 0) {
            double angle = pow(0.5, detailLevel) * steps % 1 * 2 * PI;
            if (angle > PI) {
                angle -= 2 * PI;
            }
            return angle;
        } else {
            return (-1+2*steps)*PI;
        }
    }
    
    // Returns the angle for the step given, in radians
    private double getLatitudeAngle(int steps) {
        if (detailLevel > 0) {
            return MAX_LATITUDE - pow(0.5, detailLevel) * steps * 2 * MAX_LATITUDE;
        } else {
            return (0.5-steps)*MAX_LATITUDE*2;
        }
    }

    @Override
    public double getLongitudeFrom() {
        return getLongitudeAngle(lonIndex);
    }

    @Override
    public double getLongitudeTo() {
        return getLongitudeAngle(lonIndex + 1);
    }

    @Override
    public double getLatitudeFrom() {
        return getLatitudeAngle(latIndex + 1);
    }

    @Override
    public double getLatitudeTo() {
        return getLatitudeAngle(latIndex);
    }

    /**
     * The longitude index, as supplied in the constructor.
     * 
     * @return The longitude index
     */
    public int getLongitudeIndex() {
        return lonIndex;
    }

    /**
     * The latitude index, as supplied in the constructor.
     * 
     * @return The latitude index
     */
    public int getLatitudeIndex() {
        return latIndex;
    }

    /**
     * The detail level, as supplied in the constructor.
     * 
     * @return The detail level
     */
    public int getDetailLevel() {
        return detailLevel;
    }

    /**
     * Returns a tile in the given detail level which contains a specific point.
     * 
     * @param detailLevel The detail level
     * @param coords The point
     * @return A tile
     */
    public static Tile getContainingTile(int detailLevel, GeoCoordinates coords) {
        if (detailLevel < 0 || coords == null) {
            throw new IllegalArgumentException();
        }
        
        double lonAngle = 2 * PI / pow(2, detailLevel);
        double latAngle = MAX_LATITUDE / pow(2, detailLevel);
        int lon = (int) floor(coords.getLongitude() / lonAngle);
        if (lon < 0) {
            lon = (1 << detailLevel) + lon;
        }
        int lat = (1 << (detailLevel-1)) - (int) floor((coords.getLatitude() / latAngle)) - 1;
        if (lat >= 0 && lat < (1 << detailLevel)) {
            return new OSMTile(detailLevel, lon, lat);
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "OSMTile [detailLevel=" + detailLevel + ", lonIndex=" + lonIndex + ", latIndex="
                + latIndex + ", longitudeFrom()=" + getLongitudeFrom() + ", longitudeTo()="
                + getLongitudeTo() + ", latitudeFrom()=" + getLatitudeFrom() + ", latitudeTo()="
                + getLatitudeTo() + "]";
    }
    
}
