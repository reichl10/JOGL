package de.joglearth.location.overpass;

import de.joglearth.geometry.Tile;
import de.joglearth.location.LocationType;
import de.joglearth.location.nominatim.NominatimQuery;


/**
 * Supplies a query to the OverpassAPI. All results, e.g. POIs or city names are within the FOV. The
 * size of the FOV (the associated tiles) must be part of the query.
 */
public class OverpassQuery {

    public Tile area;
    public LocationType type;


    /**
     * Constructor for a query in a given area. Assigns a value to a
     * {@link de.joglearth.surface.LocationType} and a {@link de.joglearth.geometry.Tile}.
     * 
     * @param type The <code>LocationType</code> of the query
     * @param area The <code>Tile</code> that determines where the query should be performed
     */
    public OverpassQuery(LocationType type, Tile area) {
        this.area = area;
        this.type = type;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other == null) && other.getClass() == this.getClass()) {
            OverpassQuery o = (OverpassQuery) other;
            if (o.type.equals(this.type)) {
                if ((o.area == null && this.area == null) || (o.area.equals(this.area))) {
                    return true;

                }
            }
        }

        return false;
    }
}
