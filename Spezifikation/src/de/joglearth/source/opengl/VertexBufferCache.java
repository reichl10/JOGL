package de.joglearth.source.opengl;

import javax.media.opengl.GL2;

import de.joglearth.geometry.Tile;
import de.joglearth.source.caching.MemoryCache;


/**
 * Manages and displaces vertex buffer objects in OpenGl graphics memory.
 */
public class VertexBufferCache extends MemoryCache<Tile, VertexBuffer> {

    private GL2 gl;


    /**
     * Constructor. Initializes the {@link de.joglearth.source.opengl.VertexBufferCache} and assigns
     * a value to the GL context.
     * 
     * @param gl The GL context
     */
    public VertexBufferCache(GL2 gl) {
        this.gl = gl;
    }

    @Override
    public void dropObject(Tile k) {
        VertexBuffer vboID = requestObject(k, null).value;

        if (vboID != null) {
            gl.glDeleteBuffers(1, new int[] { vboID.vertices, vboID.indices }, 0);
        }
    }
}
