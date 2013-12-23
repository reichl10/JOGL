package de.joglearth.source.opengl;

import javax.media.opengl.GL2;

import de.joglearth.opengl.GLContext;
import de.joglearth.opengl.GLError;
import de.joglearth.opengl.VertexBuffer;
import de.joglearth.rendering.Renderer;
import de.joglearth.source.caching.MemoryCache;


/**
 * Manages and displaces vertex buffer objects in OpenGl graphics memory.
 */
public class VertexBufferCache<Key> extends MemoryCache<Key, VertexBuffer> {

    private GLContext gl;

    /**
     * Constructor. Initializes the {@link de.joglearth.source.opengl.VertexBufferCache} and assigns
     * a value to the GL context.
     * 
     * @param gl The GL context
     */
    public VertexBufferCache(GLContext gl) {
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
