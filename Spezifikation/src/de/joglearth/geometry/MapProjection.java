package de.joglearth.geometry;

/**
 * A structure defining different types of projections to map one given value to a projected one
 * in a different way.
 */
public interface MapProjection {

    /**
     * Projects the longitude according to a given projection.
     * @param longitude the unprojected longitude
     * @return The projected longitude
     */
	double projectLongitude(double longitude);

    /**
     * Projects the latitude according to a given projection.
     * @param latitude the unprojected latitude
     * @return The projected latitude
     */
	double projectLatitude(double latitude);
}
