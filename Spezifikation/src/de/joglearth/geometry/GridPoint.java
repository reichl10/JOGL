package de.joglearth.geometry;


/**
 * Models an intersection of longitude and latitude lines on an arbitrary {@link TileLayout}.
 */
public final class GridPoint {

    /**
     * The point's longitude index.
     */
    public final int longitude;

    /**
     * The point's latitude index.
     */
    public final int latitude;

    /**
     * Constructor
     * @param lon The longitude index
     * @param lat The latitude index
     */
    public GridPoint(int lon, int lat) {
        this.longitude = lon;
        this.latitude = lat;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + latitude;
        result = prime * result + longitude;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GridPoint other = (GridPoint) obj;
        if (latitude != other.latitude)
            return false;
        if (longitude != other.longitude)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "(" + longitude + ", " + latitude + ")";
    }
}
