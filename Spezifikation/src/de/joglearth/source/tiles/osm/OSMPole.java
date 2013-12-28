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
        return pole == NORTH ? OSMTile.MAX_LATITUDE : -PI / 2;
    }

    @Override
    public double getLatitudeTo() {
        return pole == NORTH ? PI / 2 : OSMTile.MIN_LATITUDE;
    }

    /* (nicht-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "OSMPole [pole=" + (pole == NORTH ? "NORTH" : "SOUTH") + ", zoomLevel=" + zoomLevel + "]";
    }

}
