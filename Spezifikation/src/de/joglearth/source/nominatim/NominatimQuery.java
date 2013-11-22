package de.joglearth.source.nominatim;

import de.joglearth.geometry.ScreenCoordinates;
import de.joglearth.geometry.Tile;


/**
 * Supports a query to Nominatim. There are three Types of queries: GLOBAL, LOCAL, POINT.
 * 
 */
public class NominatimQuery {

    public Type              type;
    public Tile              area;
    public ScreenCoordinates point;
    public String            query;

    /**
     * Constructor. Assigns a value to a {@link Type}.
     * @param type The <code>Type</code> of the query.
     */
    public NominatimQuery(Type type) {
        
    }


    /**
     * Type of the NominatimQuery. GLOBAL: Global search of points. LOCAL: The results of the query
     * are within the FOV. POINT: The result of the query is a point with x and y coordinates.
     * 
     */
    public enum Type {
        GLOBAL,
        LOCAL,
        POINT
    }
}
