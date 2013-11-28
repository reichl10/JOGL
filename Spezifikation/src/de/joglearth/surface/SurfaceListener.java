package de.joglearth.surface;

import de.joglearth.geometry.Tile;


/**
 * Classes implementing this interface can be notified about individual changes of displayed tiles.
 */
public interface SurfaceListener {

    /**
     * Receives if the surface i a given area has been changed.
     * 
     * @param tile A new {@link Tile} that should be displayed now
     */
    void surfaceChanged(double lonFrom, double latFrom, double lonTo, double latTo);
}
