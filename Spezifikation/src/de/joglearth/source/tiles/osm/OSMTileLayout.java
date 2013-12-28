package de.joglearth.source.tiles.osm;

import static java.lang.Math.*;
import de.joglearth.geometry.Camera;
import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.GridPoint;
import de.joglearth.geometry.Tile;
import de.joglearth.geometry.TileLayout;


public class OSMTileLayout implements TileLayout {

    private int zoomLevel;
    private final int minLat, maxLat;
    
    public OSMTileLayout(int zoomLevel) {
        if (zoomLevel < 0) {
            throw new IllegalArgumentException();
        }
        
        this.zoomLevel = zoomLevel;
        if (zoomLevel == 0) {
            minLat = -1;
            maxLat = 1;
        } else {
            minLat = -(1 << (zoomLevel-1)) - 1;
            maxLat = 1 << (zoomLevel-1);
        }
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
            
            return new GridPoint(0, ((OSMPole) tile).pole == OSMPole.NORTH ? maxLat : minLat);
            
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
        /*double lon = 2*PI / (1 << zoomLevel) * point.getLongitude();
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
        } else if (coords.getLatitude() < OSMTile.MIN_LATITUDE) {
            return new OSMPole(zoomLevel, OSMPole.SOUTH);
        } else {
           
            double lonAngle = 2 * PI / pow(2, zoomLevel);
            double latAngle = OSMTile.MAX_LATITUDE / pow(2, zoomLevel);
            int lon = (int) floor(coords.getLongitude() / lonAngle);
            if (lon < 0) {
                lon = (1 << zoomLevel) + lon;
            }
            int lat = (1 << (zoomLevel-1)) - (int) floor((coords.getLatitude() / latAngle)) - 1;
            if (lat >= 0 && lat < (1 << zoomLevel)) {
                return new OSMTile(zoomLevel, lon, lat);
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
    public static int getOptimalzoomLevel(Camera camera, int leastHorizontalTiles) {
        return max(0, (int) ceil(log(leastHorizontalTiles / camera.getScale()) / log(2))-1);
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
            
            int pole = ((OSMPole) tile).pole;
            int slices = 1 << zoomLevel;
            GridPoint[] corners = new GridPoint[slices];
            for (int i=0; i<slices; ++i) {
                corners[i] = new GridPoint(i, pole == OSMPole.NORTH ? -1 : slices-1);
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
            lat -= getVerticalTileCount();
        }
        while (lat <= minLat - getVerticalTileCount()) {
            lat += getVerticalTileCount();
        }
        
    }
    
}
