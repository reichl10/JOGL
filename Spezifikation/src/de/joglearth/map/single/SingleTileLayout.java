package de.joglearth.map.single;

import static java.lang.Math.*;
import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.GridPoint;
import de.joglearth.geometry.Tile;
import de.joglearth.geometry.TileLayout;

/**
 * A simple implementation of the {@link TileLayout} interface used with single-image map types.
 */
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
        switch (point.latitude % 2) {
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

    @Override
    public GridPoint[] getTileCorners(Tile tile) {
        return new GridPoint[0];
    }

    @Override
    public GridPoint modulo(GridPoint point) {
        return new GridPoint(0, 0);
    }
}