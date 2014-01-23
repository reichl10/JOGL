package de.joglearth.location;

import de.joglearth.geometry.GeoCoordinates;


/**
 * Saves a points longitude, latitude and details. Is used by {@link LocationManager} to administer
 * all kinds of points on a map.
 */
public class Location implements Cloneable {

    /**
     * Coordinates of the location.
     */
    public GeoCoordinates point;
    
    /**
     * Type of the location.
     */
    public LocationType   type;
    
    /**
     * Details of the location.
     */
    public String         details;
    
    /**
     * Name of the location.
     */
    public String         name;

    /**
     * Assigns values to the local variables point, type and details.
     * 
     * @param point The {@link GeoCoordinates} of a point
     * @param type The {@link LocationType} of that specific point
     * @param details A string containing gathered details about that point
     * @param name A string containing the name of the <code>Location</code>
     *        this should be used to display it in short form
     */
    public Location(GeoCoordinates point, LocationType type, String details, String name) {
        this.point = point;
        this.details = details;
        this.type = type;
        this.name = name;
    }
}
