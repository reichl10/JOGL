package de.joglearth.surface;

import de.joglearth.geometry.GeoCoordinates;


/**
 * Saves a points longitude, latitude and details. Is used by LocationManager to administer all
 * kinds of points on a map.
 */
public class Location implements Cloneable {

    public GeoCoordinates point;
    public LocationType   type;
    public String         details;


    /**
     * Assigns values to the local variables point, type and details.
     * 
     * @param point The coordinates of a point as a tuple of longitude and latitude.
     * @param type The type of that specific point.
     * @param details A string containing gathered details about that point.
     */
    public Location(GeoCoordinates point, LocationType type, String details) {
        this.point = point;
        this.details = details;
        this.type = type;
    }
}
