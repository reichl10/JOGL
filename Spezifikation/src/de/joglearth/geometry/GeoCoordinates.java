package de.joglearth.geometry;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.floor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Structure holding longitude and latitude coordinates.
 */
// TODO Detailed description of the internal lon/lat format.
public final class GeoCoordinates implements Cloneable {

    private double longitude; // Longitude, in radians
    private double latitude; // Latitude, in radians
    private static final double ALLOWEDDIFF = 1.0E-13;


    /**
     * Constructor. Initializes coordinates by their values in radians. If a value is out of range,
     * it is automatically wrapped around.
     * 
     * @param lon Longitude, in the interval (-pi, pi]
     * @param lat Latitude, in the interval [-pi/2, pi/2]
     */
    public GeoCoordinates(double lon, double lat) {
        if (Double.isInfinite(lon) || Double.isNaN(lon) || Double.isInfinite(lat)
                || Double.isNaN(lat)) {
            throw new IllegalArgumentException("Longitude and latitude must be finite numbers");
        }

        // Wrap latitude around
        while (lat < -PI / 2 || lat > PI / 2) {
            if (lat < -PI / 2) {
                lat = -PI - lat;
            } else if (lat > PI / 2) {
                lat = PI - lat;
            }
            lon += PI;
        }

        longitude = limitRad(lon);
        latitude = lat;
    }

    // Converts an angle, given in degrees, to radians.
    private static double degToRad(double deg) {
        return deg / 180 * PI;
    }

    /**
     * Converts an angle, given in radians to degrees.
     * 
     * @param rad The value in radians
     * @return The value in degrees
     */
    private static double radToDeg(double rad) {
        return rad * 180 / PI;
    }

    /**
     * Adds or subtracts 2pi to/from an angle to move it to the interval [0, 2pi).
     * 
     * @param rad The value in radians
     * @return A converted value in the interval [0, 2pi)
     */
    private static double limitRad(double rad) {
        rad = rad % (2 * PI);
        if (rad <= -PI) {
            rad += 2 * PI;
        }
        if (rad > PI) {
            rad -= 2 * PI;
        }
        return rad;
    }

    //Defines the pattern which describes coordinates
    private static final Pattern coordinatePattern = Pattern.compile(
            "\\s*(\\d{1,3})\\s*°\\s*((\\d{1,2})\\s*'\\s*" +
                    "((\\d{1,2}(\\.\\d+)?)\\s*\"\\s*)?)?([NSEOW])\\s*");


    // TODO Allow different decimal separators
    private static double parseSingleCoordinate(String coord) {
        Matcher matcher = coordinatePattern.matcher(coord);
        if (!matcher.matches()) {
            throw new NumberFormatException();
        }

        assert matcher.groupCount() == 8;

        double deg = Double.parseDouble(matcher.group(1));
        if (matcher.group(3) != null) {
            deg += Double.parseDouble(matcher.group(3)) / 60;
        }
        if (matcher.group(5) != null) {
            deg += Double.parseDouble(matcher.group(5)) / 3600;
        }
        char direction = matcher.group(7).charAt(0);
        if (direction == 'W' || direction == 'S') {
            deg = -deg;
        }
        return degToRad(deg);
    }

    /**
     * Parses two coordinate strings, returning the {@link GeoCoordinates}.
     * 
     * @param lon The longitude, e.g. 55° 17' 48.2" E
     * @param lat The latitude, e.g. 55° 17' 48.2" N
     * @return The coordinate structure
     * @throws NumberFormatException One of the parameters was not a valid coordinate
     */
    public static GeoCoordinates parseCoordinates(String lon, String lat) {
        if (lon == null || lat == null) {
            throw new IllegalArgumentException();
        }

        double longitude = limitRad(parseSingleCoordinate(lon));
        double latitude = parseSingleCoordinate(lat);

        if (latitude < -PI || latitude > PI) {
            throw new NumberFormatException();
        }

        return new GeoCoordinates(longitude, latitude);
    }

    @Override
    public GeoCoordinates clone() {
        return new GeoCoordinates(longitude, latitude);
    }

    /**
     * Returns the longitude in radians.
     * 
     * @return The longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Returns the latitude in radians.
     * 
     * @return The latitude
     */
    public double getLatitude() {
        return latitude;
    }

    private String getCoordinateString(double coord) {
        double deg = radToDeg(abs(coord)), ideg = floor(deg), imin = Math
                .floor((deg - ideg) * 60), isec = floor((deg - ideg - (imin / 60)) * 3600);
        return String.format("%d° %02d' %02d\"", (int) ideg, (int) imin, (int) isec);
    }

    /**
     * Returns a string describing the longitude.
     * 
     * @return The longitude in string representation
     */
    public String getLongitudeString() {
        return getCoordinateString(longitude) + (longitude < 0 ? " W" : " E");
    }

    /**
     * Returns a string describing the latitude.
     * 
     * @return The latitude in string representation
     */
    public String getLatitudeString() {
        return getCoordinateString(latitude) + (latitude < 0 ? " S" : " N");
    }

    @Override
    public String toString() {
        return getLatitudeString() + " - " + getLongitudeString();
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
        return ((Math.abs(this.longitude - other.longitude)) < ALLOWEDDIFF && (Math
                .abs(this.latitude - other.latitude)) < ALLOWEDDIFF);
    }

}
