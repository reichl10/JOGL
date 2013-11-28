package de.joglearth.source.opengl;

import javax.media.opengl.GL2;

import de.joglearth.geometry.Tile;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.caching.Cache;


/**
 * Manages and displaces vertex buffer objects in OpenGl graphics memory.
 */
public class VertexBufferCache implements Cache<Tile, Integer> {

    private GL2 gl;


    /**
     * Constructor. Initializes the {@link de.joglearth.source.opengl.VertexBufferCache} and assigns a value to the GL context.
     * 
     * @param gl The GL context
     */
    public VertexBufferCache(GL2 gl) {
        this.gl = gl;
    }

    @Override
    public SourceResponse<Integer> requestObject(Tile key,
            SourceListener<Tile, Integer> sender) {
        return null;
    }

    @Override
    public void putObject(Tile k, Integer v) {

    }

    @Override
    public void dropObject(Tile k) {

    }

    @Override
    public Iterable<Tile> getExistingObjects() {
        return null;
    }

    @Override
    public void dropAll() {

    }
}
