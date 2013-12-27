package de.joglearth.source.tiles.osm;

import static java.lang.Math.*;
import de.joglearth.geometry.AbstractTile;
import de.joglearth.geometry.GridPoint;


public class OSMPole extends AbstractTile {

    public static final int NORTH = 0;
    public static final int SOUTH = 1;
    
    int pole;
    int zoomLevel;
    
    public OSMPole(int zoomLevel, int pole) {
        this.pole = pole;
        this.zoomLevel = zoomLevel;
    }
    
    @Override
    public double getLongitudeFrom() {
        return 0;
    }

    @Override
    public double getLongitudeTo() {
        return 0;
    }

    @Override
    public double getLatitudeFrom() {
        return pole == NORTH ? OSMTile.MAX_LATITUDE : -PI/2;
    }

    @Override
    public double getLatitudeTo() {
        return pole == NORTH ? PI/2 : OSMTile.MIN_LATITUDE;
    }

    @Override
    public GridPoint[] getCorners() {
        int slices = 1 << zoomLevel;
        GridPoint[] corners = new GridPoint[slices];
        for (int i=0; i<slices; ++i) {
            corners[i] = new GridPoint(i, pole == NORTH ? -1 : slices-1);
        }
        return corners;
    }

}
