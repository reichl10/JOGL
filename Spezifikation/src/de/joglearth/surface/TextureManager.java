package de.joglearth.surface;

import java.io.IOException;
import java.io.InputStream;

import javax.media.opengl.GL2;
import javax.media.opengl.GLProfile;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

import de.joglearth.geometry.Tile;
import de.joglearth.rendering.Renderer;
import de.joglearth.source.caching.RequestDistributor;
import de.joglearth.util.Resource;


/**
 * Executes requests for textures of the {@link Renderer}. Loads textures from a
 * {@link de.joglearth.source.caching.RequestDistrubutor} which accesses a
 * {@link de.joglearth.source.opengl.TileTextureSource} and a
 * {@link de.joglearth.source.opengl.TextureCache}.
 */
public class TextureManager {

    private Integer placeholderTexture;
    private RequestDistributor<Tile, Integer> source;
    private Texture placeholder;


    /**
     * Constructor.
     * 
     * @param gl The OpenGL object
     */
    public TextureManager(GL2 gl) {
        // TODO: Laden der Platzhaltertextur für Meilenstein 1. Muss evtl. später ersetzt werden.

        /* Loads placeholder-texture */
        placeholder = TextureIO.newTexture(Resource.loadTextureData("textures/placeholder.png",
                "png"));

        placeholderTexture = new Integer(placeholder.getTextureObject(gl));
    }

    /**
     * Is called if a texture of a {@link de.joglearth.geometry.Tile} should be loaded.
     * 
     * @param tile The <code>Tile</code> that should be loaded
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
