package de.joglearth.location.overpass;

import de.joglearth.geometry.Tile;
import de.joglearth.location.LocationType;


/**
 * Supplies a query to the OverpassAPI. All results, e.g. POIs or city names are within the FOV. The
 * size of the FOV (the associated tiles) must be part of the query.
 */
public class OverpassQuery {

    /**
     * The area where certain operations should be made given as a Tile.
     */
    public Tile area;
    
    /**
     * The location type that should be displayed.
     */
    public LocationType type;


    /**
     * Constructor for a query in a given area. Assigns a value to a
     * {@link LocationType} and a {@link Tile}.
     * 
     * @param type The <code>LocationType</code> of the query
     * @param area The <code>Tile</code> that determines where the query should be performed
     */
    public OverpassQuery(LocationType type, Tile area) {
        this.area = area;
        this.type = type;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((area == null) ? 0 : area.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof OverpassQuery)) {
            return false;
        }
        OverpassQuery other = (OverpassQuery) obj;
        if (area == null) {
            if (other.area != null) {
                return false;
            }
        } else if (!area.equals(other.area)) {
            return false;
        }
        if (type != other.type) {
            return false;
        }
        return true;
    }
}