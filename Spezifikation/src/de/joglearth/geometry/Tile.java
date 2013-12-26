package de.joglearth.geometry;

import static java.lang.Math.*;


/**
 * A structure identifying a single OpenStreetMap surface tile.
 */
public final class Tile implements Cloneable {

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
    public Tile(int detailLevel, int lonIndex, int latIndex) {
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

    /**
     * Returns the lower longitude bound of the tile.
     * 
     * @return The longitude, in radians
     */
    public double getLongitudeFrom() {
        return getLongitudeAngle(lonIndex);
    }

    /**
     * Returns the upper longitude bound of the tile.
     * 
     * @return The longitude, in radians
     */
    public double getLongitudeTo() {
        return getLongitudeAngle(lonIndex + 1);
    }

    /**
     * Returns the lower latitude bound of the tile.
     * 
     * @return The latitude, in radians
     */
    public double getLatitudeFrom() {
        return getLatitudeAngle(latIndex + 1);
    }

    /**
     * Returns the upper latitude bound of the tile.
     * 
     * @return The latitude, in radians.
     */
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
            return new Tile(detailLevel, lon, lat);
        } else {
            return null;
        }
    }

    /**
     * Checks if a given rectangle intersects with a {@link de.joglearth.geometry.Tile}.
     * 
     * @param lonFrom The longitude where the rectangle starts
     * @param latFrom The latitude where the rectangle starts
     * @param lonTo The longitude where the rectangle ends
     * @param latTo The latitude where the rectangle ends
     * @return True if the rectangle intersects, else false
     */
    public boolean intersects(double lonFrom, double latFrom, double lonTo, double latTo) {
        double tileLonFrom = this.getLongitudeFrom(),
                tileLatFrom = this.getLatitudeFrom(),
                tileLonTo = this.getLongitudeTo(),
                tileLatTo = this.getLatitudeTo();
        
        rectangleLongitudeContains(tileLonFrom, tileLonTo, lonTo);
        return (rectangleLongitudeContains(tileLonFrom, tileLonTo, lonTo)
                        || rectangleLongitudeContains(lonFrom, lonTo, tileLonTo)
                        || rectangleLongitudeContains(lonFrom, lonTo, tileLonFrom)
                        || rectangleLongitudeContains(tileLonFrom, tileLonTo, lonFrom))
                        &&
                ((tileLatFrom < latFrom && latFrom < tileLatTo)
                        || (tileLatFrom < latTo && latTo < tileLatTo)
                        || (latFrom < tileLatFrom && tileLatFrom < latTo)
                        || (latFrom < tileLatTo && tileLatTo < latTo));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + detailLevel;
        result = prime * result + latIndex;
        result = prime * result + lonIndex;
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
        Tile other = (Tile) obj;
        return this.detailLevel == other.detailLevel && this.latIndex == other.latIndex
                && this.lonIndex == other.lonIndex;
    }

    /**
     * Returns if given {@link de.joglearth.geometry.GeoCoordinates} are in this
     * {@link de.joglearth.geometry.Tile}.
     * 
     * @param coords The <code>GeoCoordinates</code>
     * @return If The <code>GeoCoordinates</code> are in the <code>Tile</code>
     */
    public boolean contains(GeoCoordinates coords) {
        if (coords == null) {
            throw new IllegalArgumentException();
        }
        double lon = coords.getLongitude(), lat = coords.getLatitude();
        double lonFrom = getLongitudeFrom(), latFrom = getLatitudeFrom(), 
               lonTo = getLongitudeTo(), latTo = getLatitudeTo();
        
        return rectangleLongitudeContains(lonFrom, lonTo, lon)
                && ((lat >= latFrom && lat <= latTo) || (lat <= latFrom && lat >= latTo));
    }
    
    private boolean rectangleLongitudeContains(double lonFrom, double lonTo, double lon) {

        if (lonTo <= lonFrom) {
            // TODO > 0 oder >= 0?
            if (lon > 0) {
                lonTo += 2*PI;
            } else {
                lonFrom -= 2*PI;
            }
        }
        return ((lon >= lonFrom && lon <= lonTo) || (lon <= lonFrom && lon >= lonTo));
    }

    @Override
    public String toString() {
        return "Tile [detailLevel=" + detailLevel + ", lonIndex=" + lonIndex + ", latIndex="
                + latIndex + ", longitudeFrom()=" + getLongitudeFrom() + ", longitudeTo()="
                + getLongitudeTo() + ", latitudeFrom()=" + getLatitudeFrom() + ", latitudeTo()="
                + getLatitudeTo() + "]";
    }
    
}
