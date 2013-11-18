package de.joglearth.surface;

import de.joglearth.geometry.Tile;

/**
 * 
 */
public interface SurfaceListener {
    
    /**
     * Sends a notification that the surface was changed simultaneously to a tile
     * that was changed
     * @param tile a new tile that should be displayed now
     */
	void surfaceChanged(Tile tile);
}
