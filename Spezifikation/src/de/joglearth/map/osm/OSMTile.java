package de.joglearth.map.osm;

import static java.lang.Math.*;
import de.joglearth.geometry.AbstractTile;
import de.joglearth.geometry.GeoCoordinates;


/**
 * An implementation of the {@link de.joglearth.geometry.Tile} interface modeling an OpenStreetMap
 * surface tile.
 */
public final class OSMTile extends AbstractTile {

    private int zoomLevel;
    private int lonIndex;
    private int latIndex;
    
    /**
     * The maximum latitude covered by OSM tiles, that is 85.05° N or arctan(sinh(pi)) rad.
     */
    public static final double MAX_LATITUDE = atan(sinh(PI));
    
    /**
     * The minimum latitude covered by OSM tiles, that is 85.05° S or -arctan(sinh(pi)) rad.
     */
    public static final double MIN_LATITUDE = -MAX_LATITUDE;


    /**
     * Constructor.
     * 
     * @param zoomLevel How often the globe is subdivided to reach the desired tile size. 
     *          Must not be smaller than zero
     * @param lonIndex The number of tiles to skip, starting from the north pole, to reach the
     *        desired longitude. Must be within [0, 2^zoomLevel-1]
     * @param latIndex The number of tiles to skip, starting from latitude 0, to reach the left
     *        bound of the tile. Must be within [0, 2^zoomLevel-1]
     */
    public OSMTile(int zoomLevel, int lonIndex, int latIndex) {
        if (zoomLevel < 0 || lonIndex >= (1 << zoomLevel) || lonIndex < 0 
                || latIndex >= (1 << zoomLevel) || lonIndex < 0) {
            throw new IllegalArgumentException();
        }
        
        this.zoomLevel = zoomLevel;        
        this.lonIndex = lonIndex;
        this.latIndex = latIndex;
    }

    // Returns the angle for the step given, in radians
    private double getLongitudeAngle(int steps) {
        if (zoomLevel > 0) {
            double angle = pow(0.5, zoomLevel) * steps % 1 * 2 * PI;
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
        return atan(sinh(PI - ((double) steps) / (1 << zoomLevel) * 2*PI));
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
        return zoomLevel;
    }

    @Override
    public String toString() {
        return "OSMTile [zoomLevel=" + zoomLevel + ", lonIndex=" + lonIndex + ", latIndex="
                + latIndex + ", longitudeFrom()=" + getLongitudeFrom() + ", longitudeTo()="
                + getLongitudeTo() + ", latitudeFrom()=" + getLatitudeFrom() + ", latitudeTo()="
                + getLatitudeTo() + "]";
    }
    
    public static void main(String[] args) {
        OSMTileLayout l = new OSMTileLayout(3);
        System.out.println(l.getContainingTile(new GeoCoordinates(0.1,  0.1)));
    }
    
}
