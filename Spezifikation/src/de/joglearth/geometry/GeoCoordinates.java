package de.joglearth.geometry;

/**
 * Structure holding longitude and latitude coordinates.
 */
// TODO Detailed description of the internal lon/lat format.
public final class GeoCoordinates implements Cloneable {

    private double longitude; // Longitude, in radians
    private double latitude; // Latitude, in radians


    /**
     * Constructor. Initializes coordinates by their values in radians.
     * 
     * @param lon Longitude, in the interval [0, 2pi)
     * @param lat Latitude, in the interval [-pi/2, pi/2]
     */
    public GeoCoordinates(double lon, double lat) {
        if (lat < -Math.PI / 2 || lat > Math.PI / 2) {
            throw new IllegalArgumentException("Latitude must be between -pi/2 and pi/2");
        }
        longitude = limitRad(lon);
        latitude = lat;
    }

    // Converts an angle, given in degrees, to radians.
    private static double degToRad(double deg) {
        return deg / 180 * Math.PI;
    }

    /**
     * Converts an angle, given in radians to degrees.
     * 
     * @param rad The value in radians
     * @return The value in degrees
     */
    public static double radToDeg(double rad) {
        return rad * 180 / Math.PI;
    }

    /**
     * Adds or subtracts 2pi to/from an angle to move it to the interval [0, 2pi).
     * 
     * @param rad The value in radians
     * @return A converted value in the interval [0, 2pi)
     */
    public static double limitRad(double rad) {
        while (rad < 0) {
            rad += 2 * Math.PI;
        }
        while (rad >= 2 * Math.PI) {
            rad -= 2 * Math.PI;
        }
        return rad;
    }

    /**
     * Parses two coordinate strings, returning the GeoCoordinates.
     * 
     * @param lon The longitude, e.g. 55° 17' 48.2" E
     * @param lat The latitude, e.g. 55° 17' 48.2" N
     * @return The coordinate structure
     * @throws NumberFormatException One of the parameters was not a valid coordinate
     */
    public static GeoCoordinates parseCoordinates(String lon, String lat) {
        /*
         * Coordinates are given in the format <code>/[0-9]?[0-9]\s*°\s*([0-9]?[0-9]\s*'(\s*[0-9]?[0
         * -9](\.[0-9]+)?\s*")?)?|[0-9]+(.[0-9]+)?/</code> (later referred to as "(coord)"), e.g.
         * 55° 17' 48.2" or 17.35135, followed by a specifier for North/East/South/West (see
         * parameters for details).
         */
        return null;
    }

    @Override
    public GeoCoordinates clone() {
        return new GeoCoordinates(longitude, latitude);
    }

    /**
     * Returns the longitude, in radians.
     * 
     * @return The longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Returns the latitude, in radians.
     * 
     * @return The latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Returns a string describing the longitude.
     * 
     * @return The longitude in string representation
     */
    // TODO Details?
    public String getLongitudeString() {
        return null;
    }

    /**
     * Returns a string describing the latitude.
     * 
     * @return The latitude in string representation
     */
    // TODO Details?
    public String getLatitudeString() {
        return null;
    }

    @Override
    public String toString() {
        return getLongitudeString() + " " + getLatitudeString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        GeoCoordinates other = (GeoCoordinates) obj;
        return this.longitude == other.longitude && this.latitude == other.latitude;
    }

}
