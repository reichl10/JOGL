package de.joglearth.source.opengl;

import javax.media.opengl.GL2;

import de.joglearth.opengl.GLError;
import de.joglearth.opengl.VertexBuffer;
import de.joglearth.rendering.Renderer;
import de.joglearth.source.caching.MemoryCache;


/**
 * Manages and displaces vertex buffer objects in OpenGl graphics memory.
 */
public class VertexBufferCache<Key> extends MemoryCache<Key, VertexBuffer> {

    private GL2 gl;
    private Renderer renderer;

    /**
     * Constructor. Initializes the {@link de.joglearth.source.opengl.VertexBufferCache} and assigns
     * a value to the GL context.
     * 
     * @param gl The GL context
     */
    public VertexBufferCache(Renderer renderer, GL2 gl) {
        if (gl == null) {
            throw new IllegalArgumentException();
        }
        
        this.renderer = renderer;
        this.gl = gl;
    }

    @Override
    public void dropObject(Key k) {
        System.err.println("VertexBufferCache: dropping key " + k);
        final VertexBuffer vboID = requestObject(k, null).value;

        if (vboID != null) {
            Runnable deleter = new Runnable() {
                public void run() {
                    gl.glDeleteBuffers(1, new int[] { vboID.vertices, vboID.indices }, 0);
                    GLError.throwIfActive(gl);
                };
            };
            
            if (renderer.isInsideDisplayFunction()) {
                deleter.run();
            } else {
                renderer.invokeLater(deleter);
            }
        }
    }
}
