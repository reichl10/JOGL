package de.joglearth.geometry;

/**
 * Structure holding longitude and latitude coordinates.
 */
public class GeoCoordinates implements Cloneable {

    private double longitude; // Longitude, in radians
    private double latitude; // Latitude, in radians


    /**
     * Constructor. Initializes coordinates by their values in radians.
     * 
     * @param lon Longitude, in the interval [0, 2pi).
     * @param lat Latitude, in the interval [-pi/2, pi/2].
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

    // Converts an angle, given in radians, to degrees.
    public static double radToDeg(double rad) {
        return rad * 180 / Math.PI;
    }

    // Adds or subtracts 2pi to/from an angle to move it to the interval [0, 2pi).
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
     * Coordinates are given in the format
     * /[0-9]?[0-9]\s*°\s*([0-9]?[0-9]\s*'(\s*[0-9]?[0-9](\.[0-9]+)?\s*")?)?|[0-9]+(.[0-9]+)?/
     * (later referred to as "(coord)"), e.g. 55° 17' 48.2" or 17.35135, followed by a specifier for
     * North/East/South/West (see parameters for details).
     * 
     * @param lon The longitude, in the format \s*(coord)\s*[WE]?\s*, e.g. 55° 17' 48.2" E
     * @param lat The latitude, in the format \s*(coord)\s*[NS]?\s*, e.g. 55° 17' 48.2" N
     * @return The coordinate structure.
     * @throws NumberFormatException One of the parameters was not a valid coordinate.
     */
    public static GeoCoordinates parseCoordinates(String lon, String lat) {
        return null;
    }

    @Override
    public GeoCoordinates clone() {
        return new GeoCoordinates(longitude, latitude);
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getLongitudeString() {
        return null;
    }

    public String getLatitudeString() {
        return null;
    }

    @Override
    public String toString() {
        return getLongitudeString() + " " + getLatitudeString();
    }
}
