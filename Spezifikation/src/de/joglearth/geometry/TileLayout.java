package de.joglearth.geometry;



public interface TileLayout {
    
    Tile createTile(GridPoint bottomLeft);
    
    Tile getContainingTile(GeoCoordinates geo);
    
    GeoCoordinates getGeoCoordinates(GridPoint point);    
    
    int getHoritzontalTileCount();
    
    int getVerticalTileCount();

    GridPoint getTileOrigin(Tile tile);
    
    GridPoint[] getTileCorners(Tile tile);
    
    GridPoint modulo(GridPoint point);
}
