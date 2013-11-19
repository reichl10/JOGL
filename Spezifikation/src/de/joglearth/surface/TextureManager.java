package de.joglearth.surface;

import de.joglearth.caching.RequestDistributor;
import de.joglearth.geometry.Tile;
import de.joglearth.source.SourceListener;


/**
 * 
 */
public class TextureManager {

    private Integer                           placeholderTexture;
    private RequestDistributor<Tile, Integer> source;


    /**
     * Is called if a texture of a <code>Tile</code> should be loaded.
     * @param tile The <code>Tile</code> that should be loaded.
     * @return Returns a loaded OpenGl identifier for the texture or if it is not yet loaded, the
     *         method returns a place holder texture.
     */
    public Integer getTexture(Tile tile) {
        return placeholderTexture;
    }

    /**
     * Adds a <code>SurfaceListener</code> that distributes a notification if the surface
     * was changed.
     * 
     * @param l The new listener.
     */
    public void addSurfaceListener(SurfaceListener l) {

    }

    /**
     * Removes a specific <code>SurfaceListener</code>.
     * 
     * @param l The listener that should be removed.
     */
    public void removeSurfaceListener(SurfaceListener l) {

    }

    /**
     * TODO False packet?
     * @return
     */
    public int getPendingRequests() {
        return 0;
    }
}
