package de.joglearth.geometry;

import static java.lang.Math.*;


public class OSMTileLayout implements TileLayout {

    private int zoomLevel;
    
    public OSMTileLayout(int zoomLevel) {
        this.zoomLevel = zoomLevel;
    }
    
    @Override
    public GridPoint getTileOrigin(Tile tile) {
        if (!(tile instanceof OSMTile)) {
            throw new IllegalArgumentException();
        }
        OSMTile osmTile = (OSMTile) tile;
        return new GridPoint(osmTile.getLongitudeIndex(), osmTile.getLatitudeIndex());
    }
    
    @Override
    public Tile createTile(GridPoint bottomLeft) {
        int lonIndex, latIndex;
        
        if (zoomLevel > 0) {
            lonIndex = bottomLeft.getLongitude();
            latIndex = bottomLeft.getLatitude();
            int tileCount = 1 << zoomLevel;
            
            while (latIndex >= 2*tileCount) {
                latIndex -= 2*tileCount;
            }
            while (latIndex < 0) {
                latIndex += 2*tileCount;
            }
            if (latIndex >= tileCount) {
                latIndex = 2*tileCount - 1 - latIndex;
                lonIndex += tileCount/2;
            }
            while (lonIndex >= tileCount) {
                lonIndex -= tileCount;
            }
            while (lonIndex < 0) {
                lonIndex += tileCount;
            }
        } else {
            lonIndex = 0;
            latIndex = 0;
        }
        
        return new OSMTile(zoomLevel, lonIndex, latIndex);
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
        if (zoomLevel < 0 || coords == null) {
            throw new IllegalArgumentException();
        }
        
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
    
}
