package de.joglearth.geometry;


/**
 * A structure identifying a single OpenStreetMap surface tile.
 */
public interface Tile extends Cloneable {

    /**
     * Returns the lower longitude bound of the tile.
     * 
     * @return The longitude, in radians
     */
    double getLongitudeFrom();

    /**
     * Returns the upper longitude bound of the tile.
     * 
     * @return The longitude, in radians
     */
    double getLongitudeTo();

    /**
     * Returns the lower latitude bound of the tile.
     * 
     * @return The latitude, in radians
     */
    double getLatitudeFrom();

    /**
     * Returns the upper latitude bound of the tile.
     * 
     * @return The latitude, in radians.
     */
    double getLatitudeTo();
    
    /**
     * Returns if given {@link de.joglearth.geometry.GeoCoordinates} are in this
     * {@link de.joglearth.geometry.Tile}.
     * 
     * @param coords The <code>GeoCoordinates</code>
     * @return If The <code>GeoCoordinates</code> are in the <code>Tile</code>
     */
    boolean contains(GeoCoordinates coords);
    
    /**
     * Checks if a given rectangle intersects with a {@link de.joglearth.geometry.Tile}.
     * 
     * @param lonFrom The longitude where the rectangle starts
     * @param latFrom The latitude where the rectangle starts
     * @param lonTo The longitude where the rectangle ends
     * @param latTo The latitude where the rectangle ends
     * @return True if the rectangle intersects, else false
     */
    boolean intersects(double lonFrom, double latFrom, double lonTo, double latTo);
    
    TransformedTile getScaledAlternative();
    
}
