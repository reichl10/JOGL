package de.joglearth.source.opengl;

import javax.media.opengl.GL2;

import de.joglearth.rendering.GLError;
import de.joglearth.source.caching.MemoryCache;


/**
 * Manages and displaces vertex buffer objects in OpenGl graphics memory.
 */
public class VertexBufferCache<Key> extends MemoryCache<Key, VertexBuffer> {

    private GL2 gl;

    /**
     * Constructor. Initializes the {@link de.joglearth.source.opengl.VertexBufferCache} and assigns
     * a value to the GL context.
     * 
     * @param gl The GL context
     */
    public VertexBufferCache(GL2 gl) {
        if (gl == null) {
            throw new IllegalArgumentException();
        }
        
        this.gl = gl;
    }

    @Override
    public void dropObject(Key k) {
        System.err.println("VertexBufferCache: dropping key " + k);
        VertexBuffer vboID = requestObject(k, null).value;

        if (vboID != null) {
            gl.glDeleteBuffers(1, new int[] { vboID.vertices, vboID.indices }, 0);
            GLError.throwIfActive(gl);
        }
    }
}
