package de.joglearth.map.osm;

import static java.lang.Math.*;
import de.joglearth.geometry.AbstractTile;


/**
 * A {@link de.joglearth.geometry.Tile} implementation modeling a pole in the OpenStreetMap tile
 * layout.
 */
public class OSMPole extends AbstractTile {

    /**
     * Enumeration value to identify the north pole.
     */
    public static final int NORTH = 0;
    
    /**
     * Enumeration value to identify the south pole.
     */
    public static final int SOUTH = 1;

    private final int pole;
    private final int zoomLevel;

    /**
     * Constructor.
     * @param zoomLevel The zoom level. Must not be smaller than zero
     * @param pole The pole. Must be either <code>NORTH</code> or <code>SOUTH</code>.
     */
    public OSMPole(int zoomLevel, int pole) {
        if (zoomLevel < 0 || (pole != NORTH && pole != SOUTH)) {
            throw new IllegalArgumentException();
        }
        
        this.pole = pole;
        this.zoomLevel = zoomLevel;
    }
    
    /**
     * Returns which pole is described by the OSMPole instance.
     * @return The pole, i.e. either <code>NORTH</code> or <code>SOUTH</code>.
     */
    public int getPole() {
        return pole;
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