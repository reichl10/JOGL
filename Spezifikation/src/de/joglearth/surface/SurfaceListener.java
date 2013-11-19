package de.joglearth.surface;

import de.joglearth.geometry.Tile;


/**
 * Classes implementing this interface can be notified about individual changes of displayed tiles.
 */
public interface SurfaceListener {

    /**
     * Sends a notification that the surface was changed simultaneously to a tile that was changed.
     * This can happen if a texture is loaded too late or a user added a marker.
     * 
     * @param tile A new tile that should be displayed now
     */
    void surfaceChanged(Tile tile);
}
