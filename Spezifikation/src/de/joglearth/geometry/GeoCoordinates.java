package de.joglearth.geometry;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * @param lon Longitude, in the interval (-pi, pi]
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
        while (rad <= -Math.PI) {
            rad += 2 * Math.PI;
        }
        while (rad > Math.PI) {
            rad -= 2 * Math.PI;
        }
        return rad;
    }
    
    
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
        
        for (int i=0; i<=matcher.groupCount(); ++i) System.out.println(matcher.group(i));
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
     * Parses two coordinate strings, returning the GeoCoordinates.
     * 
     * @param lon The longitude, e.g. 55° 17' 48.2" E
     * @param lat The latitude, e.g. 55° 17' 48.2" N
     * @return The coordinate structure
     * @throws NumberFormatException One of the parameters was not a valid coordinate
     */
    public static GeoCoordinates parseCoordinates(String lon, String lat) {
        double longitude = limitRad(parseSingleCoordinate(lon)), 
               latitude = parseSingleCoordinate(lat);

        if (latitude < -Math.PI || latitude > Math.PI) {
            throw new NumberFormatException();
        }

        return new GeoCoordinates(longitude, latitude);
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

    private String getCoordinateString(double coord) {
        double deg = radToDeg(Math.abs(coord)), ideg = Math.floor(deg), imin = Math
                .floor((deg - ideg) * 60), isec = Math.floor((deg - ideg - imin) * 3600);
        if (isec == (int) isec) {
            return String.format("%d° %d' %d\"", (int) ideg, (int) imin, (int) isec);
        } else {
            return String.format("%d° %d' %s\"", (int) ideg, (int) imin, Double.toString(isec));
        }
    }

    /**
     * Returns a string describing the longitude.
     * 
     * @return The longitude in string representation
     */
    // TODO Details?
    public String getLongitudeString() {
        boolean west = longitude > Math.PI;
        return getCoordinateString(west ? 2 * Math.PI - longitude : longitude)
                + (west ? " W" : " E");
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
        return this.longitude == other.longitude && this.latitude == other.latitude;
    }
    
}
