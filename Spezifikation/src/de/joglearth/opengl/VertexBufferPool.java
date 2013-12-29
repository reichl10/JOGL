package de.joglearth.opengl;

import de.joglearth.source.caching.MemoryCache;


/**
 * Manages and displaces vertex buffer objects in OpenGl graphics memory.
 */
public class VertexBufferPool<Key> extends MemoryCache<Key, VertexBuffer> {

    private GLContext gl;

    /**
     * Constructor. Initializes the {@link de.joglearth.opengl.VertexBufferPool} and assigns
     * a value to the GL context.
     * 
     * @param gl The GL context. Must not be null
     */
    public VertexBufferPool(GLContext gl) {
        if (gl == null) {
            throw new IllegalArgumentException();
        }
        
        this.gl = gl;
    }

    @Override
    public void dropObject(Key k) {
        //TODO System.err.println("VertexBufferCache: dropping key " + k);
        final VertexBuffer vbo = requestObject(k, null).value;

        if (vbo != null) {
            gl.invokeSooner(new Runnable() {
                public void run() {
                    gl.deleteVertexBuffer(vbo);
                };
            });
        }
    }
}
