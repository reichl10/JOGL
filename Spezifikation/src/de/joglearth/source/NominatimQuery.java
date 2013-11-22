package de.joglearth.source;

import de.joglearth.geometry.ScreenCoordinates;
import de.joglearth.geometry.Tile;


/**
 * Supports a query to Nominatim. There are three types of queries: GLOBAL, LOCAL, POINT.
 * 
 */
public class NominatimQuery {

    public Type              type;
    public Tile              area;
    public ScreenCoordinates point;
    public String            query;


    /**
     * Constructor. Assigns a value to a {@link Type}.
     * 
     * @param type The <code>Type</code> of the query
     */
    public NominatimQuery(Type type) {

    }


    /**
     * <code>Type</code> of the NominatimQuery.
     * 
     */
    public enum Type {
        /**
         * Global search of points.
         */
        GLOBAL,

        /**
         * The results of the query are within the FOV.
         */
        LOCAL,

        /**
         * The result of the query is a point with x and y coordinates.
         */
        POINT
    }
}
