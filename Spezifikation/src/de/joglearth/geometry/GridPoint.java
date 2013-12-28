package de.joglearth.geometry;


// Models an intersection of longitude and latitude lines
public final class GridPoint {

    private int lon, lat;


    public int getLongitude() {
        return lon;
    }

    public int getLatitude() {
        return lat;
    }

    public GridPoint(int lon, int lat) {
        this.lon = lon;
        this.lat = lat;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + lat;
        result = prime * result + lon;
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
        if (lat != other.lat)
            return false;
        if (lon != other.lon)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "(" + lon + ", " + lat + ")";
    }
}
