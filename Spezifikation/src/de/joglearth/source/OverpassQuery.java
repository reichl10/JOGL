package de.joglearth.source;

import de.joglearth.geometry.Tile;
import de.joglearth.surface.LocationType;

/**
 * The class OverpassQuery supports a query to the OverpassAPI. All results, e.g. POIs or citynames
 * are within the FOV. The size of the FOV (the associated tiles) must be part of the query.
 * 
 */
public class OverpassQuery {

    public Tile         area;
    public LocationType query;
}
