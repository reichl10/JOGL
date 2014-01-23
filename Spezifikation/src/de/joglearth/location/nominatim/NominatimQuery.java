package de.joglearth.location.nominatim;

import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.Tile;


/**
 * Supports a query to Nominatim. There are three types of queries: GLOBAL, LOCAL, POINT.
 * 
 */
public class NominatimQuery {

    /**
     * Query type.
     */
    public Type type;

    /**
     * The area of the query.
     */
    public Tile area;

    /**
     * Coordinates of the point.
     */
    public GeoCoordinates point;

    /**
     * The query.
     */
    public String query;


    /**
     * Constructor. Assigns a value to a {@link de.joglearth.source.nominatim.Type}.
     * 
     * @param type The <code>Type</code> of the query
     */
    public NominatimQuery(Type type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        StringBuffer sBuffer = new StringBuffer();
        if (point != null) {
            sBuffer.append(point.toString());
        }
        if (query != null) {
            sBuffer.append(query);
        }
        if (area != null) {
            sBuffer.append(area);
        }
        sBuffer.append(type.name());
        return sBuffer.toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other == null) && other.getClass() == this.getClass()) {
            NominatimQuery o = (NominatimQuery) other;
            if (o.type.equals(this.type)) {
                if ((o.area == null && this.area == null) || (o.area.equals(this.area))) {
                    if ((o.point == null && this.point == null) || (o.point.equals(this.point))) {
                        return true;
                    }
                }
            }
        }

        return false;
    }


    /**
     * {@link de.joglearth.source.nominatim.Type} of the
     * {@link de.joglearth.location.nominatim.NominatimQuery}.
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
