package de.joglearth.map.osm;

import static java.lang.Math.*;
import de.joglearth.geometry.AbstractTile;
import de.joglearth.geometry.Matrix4;
import de.joglearth.geometry.TransformedTile;


/**
 * An implementation of the {@link Tile} interface modeling an OpenStreetMap
 * surface tile.
 */
public final class OSMTile extends AbstractTile {

    private int zoomLevel;
    private int lonIndex;
    private int latIndex;
    
    private Double longitudeFrom, longitudeTo, latitudeFrom, latitudeTo;
    
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
        return ((double) steps) / (1 << zoomLevel) * 2*PI - PI;
    }
    
    // Returns the angle for the step given, in radians
    private double getLatitudeAngle(int steps) {
        return atan(sinh(PI - ((double) steps) / (1 << zoomLevel) * 2*PI));
    }

    @Override
    public double getLongitudeFrom() {
        if (longitudeFrom == null) {
            longitudeFrom = getLongitudeAngle(lonIndex);
        }
        return longitudeFrom;
    }

    @Override
    public double getLongitudeTo() {
        if (longitudeTo == null) {
            longitudeTo = getLongitudeAngle(lonIndex + 1);
        }
        return longitudeTo;
    }

    @Override
    public double getLatitudeFrom() {
        if (latitudeFrom == null) {
            latitudeFrom = getLatitudeAngle(latIndex + 1);
        }
        return latitudeFrom;
    }

    @Override
    public double getLatitudeTo() {
        if (latitudeTo == null) {
            latitudeTo = getLatitudeAngle(latIndex);
        }
        return latitudeTo;
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
    public TransformedTile getScaledAlternative() {
        if (zoomLevel == 0) {
            return null;
        } else {
            Matrix4 transformation = Matrix4.IDENTITY
                    .scale(0.5, 0.5, 1)
                    .translate(lonIndex % 2, 1 - latIndex % 2, 0);
            OSMTile scaledTile = new OSMTile(zoomLevel-1, lonIndex / 2, latIndex / 2);
            return new TransformedTile(scaledTile, transformation);
        }
    }

    @Override
    public String toString() {
        return "OSMTile [zoomLevel=" + zoomLevel + ", lonIndex=" + lonIndex + ", latIndex="
                + latIndex + ", longitudeFrom()=" + getLongitudeFrom() + ", longitudeTo()="
                + getLongitudeTo() + ", latitudeFrom()=" + getLatitudeFrom() + ", latitudeTo()="
                + getLatitudeTo() + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + latIndex;
        result = prime * result + lonIndex;
        result = prime * result + zoomLevel;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof OSMTile)) {
            return false;
        }
        OSMTile other = (OSMTile) obj;
        if (latIndex != other.latIndex) {
            return false;
        }
        if (lonIndex != other.lonIndex) {
            return false;
        }
        if (zoomLevel != other.zoomLevel) {
            return false;
        }
        return true;
    }
}
