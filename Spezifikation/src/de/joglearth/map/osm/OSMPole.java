package de.joglearth.map.osm;

import static java.lang.Math.*;
import de.joglearth.geometry.AbstractTile;
import de.joglearth.geometry.TransformedTile;


/**
 * A {@link Tile} implementation modeling a pole in the OpenStreetMap tile
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
    
    /**
     * Determines the actual zoom level.
     * @return The zoom level
     */
    public int getZoomLevel() {
        return zoomLevel;
    }

    @Override
    public double getLongitudeFrom() {
        return -PI;
    }

    @Override
    public double getLongitudeTo() {
        return PI;
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

    @Override
    public TransformedTile getScaledAlternative() {
        return null;
    }
}
