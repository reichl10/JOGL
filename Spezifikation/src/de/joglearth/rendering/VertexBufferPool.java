package de.joglearth.rendering;

import de.joglearth.opengl.GLContext;
import de.joglearth.opengl.VertexBuffer;
import de.joglearth.source.caching.MemoryCache;


/**
 * Manages and displaces vertex buffer objects in OpenGl graphics memory.
 */
public class VertexBufferPool<Key> extends MemoryCache<Key, VertexBuffer> {

    private GLContext gl;

    /**
     * Constructor. Initializes the {@link de.joglearth.rendering.VertexBufferPool} and assigns
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
        final VertexBuffer vbo = requestObject(k, null).value;

        if (vbo != null) {
            gl.invokeLater(new Runnable() {
                public void run() {
                    gl.deleteVertexBuffer(vbo);
                };
            });
        }
        super.dropObject(k);
    }
}
