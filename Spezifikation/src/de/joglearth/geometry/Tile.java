package de.joglearth.geometry;

/**
 * A structure identifying a single OpenStreetMap surface tile.
 */
public class Tile implements Cloneable {

    private int detailLevel;
    private int lonIndex;
    private int latIndex;


    /**
     * Constructor.
     * 
     * @param detailLevel How often the globe is subdivided to reach the desired tile size.
     * @param lonIndex The number of tiles to skip, starting from latitude 0, to reach the left
     *        bound of the tile.
     * @param lonIndex The number of tiles to skip, starting from the north pole, to reach the
     *        desired longitude.
     */
    public Tile(int detailLevel, int lonIndex, int latIndex)
    {
        this.detailLevel = detailLevel;
        this.lonIndex = lonIndex;
        this.latIndex = latIndex;
    }

    // Returns the angle for the step given, in radians
    private float angle(int steps) {
        return (float) (Math.pow(0.5, detailLevel) * steps % 1 * 2 * Math.PI);
    }

    /**
     * Returns the lower longitude bound of the tile.
     * 
     * @return The longitude, in radians.
     */
    public float longitudeFrom() {
        return angle(lonIndex);
    }

    /**
     * Returns the upper longitude bound of the tile.
     * 
     * @return The longitude, in radians.
     */
    public float longitudeTo() {
        return angle(lonIndex + 1);
    }

    /**
     * Returns the lower latitude bound of the tile.
     * 
     * @return The latitude, in radians.
     */
    public float latitudeFrom() {
        return angle(latIndex);
    }

    /**
     * Returns the upper latitude bound of the tile.
     * 
     * @return The latitude, in radians.
     */
    public float latitudeTo() {
        return angle(latIndex + 1);
    }

    /**
     * The longitude index, as supplied in the constructor.
     * 
     * @return The longitude index.
     */
    public int getLongitudeIndex() {
        return lonIndex;
    }

    /**
     * The latitude index, as supplied in the constructor.
     * 
     * @return The latitude index.
     */
    public int getLatitudeIndex() {
        return latIndex;
    }

    /**
     * The detail level, as supplied in the constructor.
     * 
     * @return The detail level.
     */
    public int getDetailLevel() {
        return detailLevel;
    }
    
    /**
     * Returns a tile in the given detail level which contains a specific point.
     * @param detailLevel The detail level.
     * @param coords The point.
     * @return A tile.
     */
    public static Tile getContainingTile(int detailLevel, GeoCoordinates coords) {
        return null;
    }
}
