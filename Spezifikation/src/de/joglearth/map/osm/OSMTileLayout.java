package de.joglearth.map.osm;

import static java.lang.Math.*;
import de.joglearth.geometry.Camera;
import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.GridPoint;
import de.joglearth.geometry.Tile;
import de.joglearth.geometry.TileLayout;


/**
 * A {@link TileLayout} modeling the OpenStreetMap tile partition for a fixed zoom level.
 */
public class OSMTileLayout implements TileLayout {

    private final int zoomLevel;
    private final int minLon, maxLon, minLat, maxLat;
    
    /**
     * Constructor.    
     * @param zoomLevel The zoom level. Must not be smaller than zero.
     */
    public OSMTileLayout(int zoomLevel) {
        if (zoomLevel < 0) {
            throw new IllegalArgumentException();
        }
        
        this.zoomLevel = zoomLevel;
        if (zoomLevel == 0) {
            minLon = maxLon = 0;
        } else {
            minLon = -(1 << (zoomLevel-1));
            maxLon = (1 << (zoomLevel-1)) - 1;
        }
        minLat = minLon - 1;
        maxLat = maxLon + 1;
    }
    
    @Override
    public GridPoint getTileOrigin(Tile tile) {
        if (tile instanceof OSMTile) {
            
            int lon = ((OSMTile) tile).getLongitudeIndex(), 
                lat = ((OSMTile) tile).getLatitudeIndex();
            if (zoomLevel > 0 && lon >= 1 << (zoomLevel-1)) {
                lon = lon - (1 << zoomLevel);
            }
            return new GridPoint(lon, (1 << (zoomLevel-1)) - 1 - lat);
            
        } else if (tile instanceof OSMPole) {
            
            return new GridPoint(0, ((OSMPole) tile).getPole() == OSMPole.NORTH ? maxLat : minLat);
            
        } else {
            throw new IllegalArgumentException();
        }
    }
    
    @Override
    public Tile createTile(GridPoint bottomLeft) {
        GridPoint moduloPoint = modulo(bottomLeft);
        
        int lon = moduloPoint.getLongitude(), lat = moduloPoint.getLatitude();
                
        if (lat == maxLat) {
            return new OSMPole(zoomLevel, OSMPole.NORTH);
        } else if (lat == minLat) {
            return new OSMPole(zoomLevel, OSMPole.SOUTH);
        } else if (zoomLevel == 0) {
            return new OSMTile(0, 0, 0);
        } else {
            if (lon < 0) {
                lon += (1 << zoomLevel);
            }
            return new OSMTile(zoomLevel, lon, (1 << (zoomLevel-1))-1-lat);
        }
    }

    @Override
    public GeoCoordinates getGeoCoordinates(GridPoint point) {
        /* TODO could this be implemented via modulo() ?
         * double lon = 2*PI / (1 << zoomLevel) * point.getLongitude();
        double lat = OSMTile.MAX_LATITUDE * (1 - 2 / (1 << zoomLevel) * point.getLatitude());
        return new GeoCoordinates(lon, lat);*/
        Tile tile = createTile(point);
        return new GeoCoordinates(tile.getLongitudeFrom(), tile.getLatitudeFrom());
    }

    @Override
    public Tile getContainingTile(GeoCoordinates coords) {
        if (coords == null) {
            throw new IllegalArgumentException();
        }
        
        if (coords.getLatitude() >= OSMTile.MAX_LATITUDE) {
            return new OSMPole(zoomLevel, OSMPole.NORTH);
        } else if (coords.getLatitude() <= OSMTile.MIN_LATITUDE) {
            return new OSMPole(zoomLevel, OSMPole.SOUTH);
        } else {
           
            int lonIndex = (int) floor((coords.getLongitude() + PI) / (2*PI) * (1 << zoomLevel));
            if (lonIndex == (1 << zoomLevel)) {
                lonIndex = 0;
            }
            
            int latIndex = (int) floor((1-
                    (log(tan(coords.getLatitude()) + 1/cos(coords.getLatitude()))) / PI) 
                    * (1 << (zoomLevel-1)));
            
            if (latIndex >= 0 && latIndex < (1 << zoomLevel)) {
                return new OSMTile(zoomLevel, lonIndex, latIndex);
            } else {
                return null;
            }
        }
    }


    /**
     * Calculates the optimal zoom level so that at least <code>leastHorizontalTiles</code>^2 tiles
     * are displayed
     * 
     * @param camera The current camera
     * @param leastHorizontalTiles The least number of horizontal tiles that should be displayed
     * @return The optimal level of the tiles
     */
    public static int getOptimalZoomLevel(Camera camera, int leastHorizontalTiles) {
        return min(max(0, 
                (int) ceil(log(leastHorizontalTiles / camera.getScale()) / log(2))-1), 18);
    }

    @Override
    public int getHoritzontalTileCount() {
        return 1 << zoomLevel;
    }

    @Override
    public int getVerticalTileCount() {
        return (1 << zoomLevel) + 2; // 2 poles
    }

    @Override
    public GridPoint[] getTileCorners(Tile tile) {
        if (tile instanceof OSMTile) {
            
            GridPoint origin = getTileOrigin(tile);
            return new GridPoint[] { 
                    origin, new GridPoint(origin.getLongitude() + 1, origin.getLatitude()),
                    new GridPoint(origin.getLongitude(), origin.getLatitude() + 1),
                    new GridPoint(origin.getLongitude() + 1, origin.getLatitude() + 1) };
            
        } else if (tile instanceof OSMPole) {
            
            int pole = ((OSMPole) tile).getPole();
            int slices = 1 << zoomLevel;
            GridPoint[] corners = new GridPoint[slices];
            for (int i=0; i<slices; ++i) {
                corners[i] = new GridPoint(i, pole == OSMPole.NORTH ? maxLat : minLat);
            }
            return corners;
            
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public GridPoint modulo(GridPoint point) {
        int lon = point.getLongitude(), lat = point.getLatitude();
        while (lat >= maxLat + getVerticalTileCount()) {
            lat -= 2*getVerticalTileCount();
        }
        while (lat <= minLat - getVerticalTileCount()) {
            lat += 2*getVerticalTileCount();
        }
        if (zoomLevel > 0 && (lat > maxLat || lat < minLat)) {
            lon += getHoritzontalTileCount() / 2;
        }
        if (lat > maxLat) {
            lat = 2*maxLat+1 - lat;
        } else if (lat < minLat) {
            lat = 2*minLat-1 - lat;
        }        
        while (lon > maxLon) {
            lon -= (1 << zoomLevel);
        }
        while (lon < minLon) {
            lon += (1 << zoomLevel);
        }
        if (lat == minLat || lat == maxLat) {
            lon = 0;
        }
        GridPoint result = new GridPoint(lon, lat);
        return result;
    }
    
}
