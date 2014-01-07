package de.joglearth.location.overpass;

import de.joglearth.geometry.Tile;
import de.joglearth.location.LocationType;


/**
 * Supplies a query to the OverpassAPI. All results, e.g. POIs or city names are within the FOV. The
 * size of the FOV (the associated tiles) must be part of the query.
 */
public class OverpassQuery {

    public Tile area;
    public LocationType query;
    


    /**
<<<<<<< HEAD:Spezifikation/src/de/joglearth/location/overpass/OverpassQuery.java
     * Constructor for a query in a given area. Assigns a value to a {@link de.joglearth.location.LocationType} and a
     * {@link de.joglearth.geometry.Tile}.
=======
     * Constructor for a query in a given area. Assigns a value to a
     * {@link de.joglearth.surface.LocationType} and a {@link de.joglearth.geometry.Tile}.
>>>>>>> tiles:Spezifikation/src/de/joglearth/source/overpass/OverpassQuery.java
     * 
     * @param query The <code>LocationType</code> of the query
     * @param area The <code>Tile</code> that determines where the query should be performed
     */
    public OverpassQuery(LocationType query, Tile area) {

    }
}
