package de.joglearth.geometry;

/**
 * A structure identifying a single OpenStreetMap surface tile.
 */
public final class Tile implements Cloneable {

    private int detailLevel;
    private int lonIndex;
    private int latIndex;


    /**
     * Constructor.
     * 
     * @param detailLevel How often the globe is subdivided to reach the desired tile size
     * @param lonIndex The number of tiles to skip, starting from latitude 0, to reach the left
     *        bound of the tile
     * @param lonIndex The number of tiles to skip, starting from the north pole, to reach the
     *        desired longitude
     */
    public Tile(int detailLevel, int lonIndex, int latIndex)
    {
        this.detailLevel = detailLevel;
        this.lonIndex = lonIndex;
        this.latIndex = latIndex;
    }

    // Returns the angle for the step given, in radians
    private double angle(int steps) {
        return Math.pow(0.5, detailLevel) * steps % 1 * 2 * Math.PI;
    }

    /**
     * Returns the lower longitude bound of the tile.
     * 
     * @return The longitude, in radians
     */
    public double longitudeFrom() {
        return angle(lonIndex);
    }

    /**
     * Returns the upper longitude bound of the tile.
     * 
     * @return The longitude, in radians
     */
    public double longitudeTo() {
        return angle(lonIndex + 1);
    }

    /**
     * Returns the lower latitude bound of the tile.
     * 
     * @return The latitude, in radians
     */
    public double latitudeFrom() {
        return angle(latIndex);
    }

    /**
     * Returns the upper latitude bound of the tile.
     * 
     * @return The latitude, in radians.
     */
    public double latitudeTo() {
        return angle(latIndex + 1);
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
        return null;
    }
    
    
    /**
     * 
     * @param lonFrom
     * @param latFrom
     * @param lonTo
     * @param latTo
     * @return
     */
    public boolean intersects(double lonFrom, double latFrom, double lonTo, double latTo) {
        return false;
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
     * Returns if given {@link GeoCoordinates} are in this tile.
     * 
     * @param coords Coordinates
     * @return  If coords are in the Tile
     */
    public boolean contains(GeoCoordinates coords) {
        if (coords == null) {
            throw new IllegalArgumentException();
        }
        double lon = coords.getLongitude(), lat = coords.getLatitude();
        return lon >= longitudeFrom() && lon <= longitudeTo() && lat >= latitudeFrom()
                && lat <= latitudeTo();
    }
}
