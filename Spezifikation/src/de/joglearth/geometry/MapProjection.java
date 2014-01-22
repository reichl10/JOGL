package de.joglearth.geometry;

public interface MapProjection {

    /**
     * //TODO
     * @param longitude
     * @return
     */
	double projectLongitude(double longitude);
	
	/**
	 *  //TODO
	 * @param latitude
	 * @return
	 */
	double projectLatitude(double latitude);
}
