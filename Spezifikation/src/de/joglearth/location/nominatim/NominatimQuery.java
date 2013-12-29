package de.joglearth.location.nominatim;

import de.joglearth.geometry.ScreenCoordinates;
import de.joglearth.geometry.Tile;


/**
 * Supports a query to Nominatim. There are three types of queries: GLOBAL, LOCAL, POINT.
 * 
 */
public class NominatimQuery {

    /**
     * Query type.
     */
    public Type              type;
    
    /**
     * The area of the query.
     */
    public Tile              area;
    
    /**
     * Coordinates of the point.
     */
    public ScreenCoordinates point;
    
    /**
     * The query.
     */
    public String            query;

    /**
     * Constructor. Assigns a value to a {@link de.joglearth.source.nominatim.Type}.
     * 
     * @param type The <code>Type</code> of the query
     */
    public NominatimQuery(Type type) {

    }


    /**
     * {@link de.joglearth.source.nominatim.Type} of the {@link de.joglearth.location.nominatim.NominatimQuery}.
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
