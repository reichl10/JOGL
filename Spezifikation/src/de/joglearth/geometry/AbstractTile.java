package de.joglearth.geometry;

import static java.lang.Math.PI;


/**
 * Implements the layout-independent geometric methods of {@link Tile}.
 * 
 * Tiles of a sub-type of AbstractTile are considered equal if they have equal boundaries.
 */
public abstract class AbstractTile implements Tile {

    @Override
    public boolean intersects(double lonFrom, double latFrom, double lonTo, double latTo) {
        double tileLonFrom = this.getLongitudeFrom(),
                tileLatFrom = this.getLatitudeFrom(),
                tileLonTo = this.getLongitudeTo(),
                tileLatTo = this.getLatitudeTo();
        
        rectangleLongitudeContains(tileLonFrom, tileLonTo, lonTo);
        return (rectangleLongitudeContains(tileLonFrom, tileLonTo, lonTo)
                        || rectangleLongitudeContains(lonFrom, lonTo, tileLonTo)
                        || rectangleLongitudeContains(lonFrom, lonTo, tileLonFrom)
                        || rectangleLongitudeContains(tileLonFrom, tileLonTo, lonFrom))
                        &&
                ((tileLatFrom < latFrom && latFrom < tileLatTo)
                        || (tileLatFrom < latTo && latTo < tileLatTo)
                        || (latFrom < tileLatFrom && tileLatFrom < latTo)
                        || (latFrom < tileLatTo && tileLatTo < latTo));
    }


    @Override
    public boolean contains(GeoCoordinates coords) {
        if (coords == null) {
            throw new IllegalArgumentException();
        }
        double lon = coords.getLongitude(), lat = coords.getLatitude();
        double lonFrom = getLongitudeFrom(), latFrom = getLatitudeFrom(), 
               lonTo = getLongitudeTo(), latTo = getLatitudeTo();
        
        return rectangleLongitudeContains(lonFrom, lonTo, lon)
                && ((lat >= latFrom && lat <= latTo) || (lat <= latFrom && lat >= latTo));
    }
    
    private boolean rectangleLongitudeContains(double lonFrom, double lonTo, double lon) {

        if (lonTo <= lonFrom) {
            // TODO > 0 oder >= 0?
            if (lon > 0) {
                lonTo += 2*PI;
            } else {
                lonFrom -= 2*PI;
            }
        }
        return ((lon >= lonFrom && lon <= lonTo) || (lon <= lonFrom && lon >= lonTo));
    }
    
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } 
        if (!(obj instanceof Tile)) {
            return false;
        }
        Tile other = (Tile) obj;
        return this.getLatitudeFrom() == other.getLatitudeFrom() 
                && this.getLatitudeTo() == other.getLatitudeTo()
                && this.getLongitudeFrom() == other.getLongitudeFrom()
                && this.getLongitudeTo() == other.getLongitudeTo();
    }
    
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(getLatitudeFrom());
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getLongitudeFrom());
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getLatitudeTo());
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getLongitudeTo());
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

}
