package de.joglearth.source.overpass;

import de.joglearth.geometry.Tile;
import de.joglearth.surface.LocationType;


/**
 * Supplies a query to the OverpassAPI. All results, e.g. POIs or city names are within the FOV. The
 * size of the FOV (the associated tiles) must be part of the query.
 */
public class OverpassQuery {

    public Tile         area;
    public LocationType query;


    /**
     * Constructor for a query in a given area. Assigns a value to a {@link LocationType} and a
     * {@link Tile}.
     * 
     * @param query The <code>LocationType</code> of the query
     * @param area The <code>Tile</code> that determines where the query should be performed
     */
    public OverpassQuery(LocationType query, Tile area) {

    }
}
