package de.joglearth.height;

import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.SurfaceListener;


/**
 * Interface that returns certain values for height requests.
 */
public interface HeightMap {

    /**
     * The maximum height value allowed as a result of getHeight(), around 638 km above sea level.
     */
    static final double MAX_HEIGHT = 0.1;

    /**
     * The minimum height value allowed as a result of getHeight(), around 638 km below sea level.
     */
    static final double MIN_HEIGHT = -MAX_HEIGHT;

    /**
     * The height in meters of a height value of "1" returned by getHeight.
     */
    static final double HEIGHT_UNIT_METERS = 6_378_137;


    /**
     * Calculates the height at a given point, interpolated to fit a grid with angularResolution
     * sized longitude / latitude steps. It is scaled equally to the surface coordinate's radians
     * values, meaning that height "0" is sea level and "1" has a magnitude of the earth's mean
     * radius (around 6378 km, or exactly HEIGHT_UNIT_METERS).
     * 
     * @param coords The coordinates of the point in question
     * @param angularResolution The grid size to interpolate for
     * @return The height above sea level, a value between (including) MIN_HEIGHT and MAX_HEIGHT.
     */
    double getHeight(GeoCoordinates coords, double angularResolution);

    /**
     * Adds a {@link SurfaceListener} that is notified if missing height data arrives.
     * 
     * @param l The new listener
     */
    void addSurfaceListener(SurfaceListener l);

    /**
     * Removes a specific {@link SurfaceListener}.
     * 
     * @param l The listener to remove
     */
    void removeSurfaceListener(SurfaceListener l);

    @Override
    int hashCode();

    @Override
    boolean equals(Object other);

}
