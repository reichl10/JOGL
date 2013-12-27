package de.joglearth.source.tiles.single;

import static java.lang.Math.*;
import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.GridPoint;
import de.joglearth.geometry.Tile;
import de.joglearth.geometry.TileLayout;


public class SingleTileLayout implements TileLayout {

    @Override
    public Tile createTile(GridPoint bottomLeft) {
        return new SingleTile();
    }

    @Override
    public Tile getContainingTile(GeoCoordinates geo) {
        return new SingleTile();
    }

    @Override
    public GeoCoordinates getGeoCoordinates(GridPoint point) {
        switch (point.getLatitude() % 2) {
            case 0: return new GeoCoordinates(0, -PI/2);
            default: return new GeoCoordinates(0, PI/2);
        }
    }

    @Override
    public int getHoritzontalTileCount() {
        return 1;
    }

    @Override
    public int getVerticalTileCount() {
        return 1;
    }

    @Override
    public GridPoint getTileOrigin(Tile tile) {
        return new GridPoint(0, 0);
    }

}
