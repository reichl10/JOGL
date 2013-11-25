package de.joglearth.surface;

import javax.media.opengl.GL2;

import de.joglearth.geometry.Tile;
import de.joglearth.source.SourceListener;
import de.joglearth.source.caching.RequestDistributor;


/**
 * Executes requests for textures of the {@link Renderer}. Loads textures from a
 * {@link RequestDistrubutor} which accesses a {@link TileTextureSource} and a {@link TextureCache}.
 */
public class TextureManager {

    private Integer placeholderTexture;
    private RequestDistributor<Tile, Integer> source;


    public TextureManager(GL2 gl) {}

    /**
     * Is called if a texture of a {@link Tile} should be loaded.
     * 
     * @param tile The <code>Tile</code> that should be loaded.
     * @return Returns a loaded OpenGl identifier for the texture or if it is not yet loaded, the
     *         method returns a place holder texture
     */
    public Integer getTexture(Tile tile) {
        return placeholderTexture;
    }

    /**
     * Adds a {@link SurfaceListener} that distributes a notification if the surface was changed.
     * 
     * @param l The new listener
     */
    public void addSurfaceListener(SurfaceListener l) {

    }

    /**
     * Removes a specific {@link SurfaceListener}.
     * 
     * @param l The listener that should be removed
     */
    public void removeSurfaceListener(SurfaceListener l) {

    }
}
