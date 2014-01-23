package de.joglearth.rendering;

import com.jogamp.opengl.util.texture.Texture;

import de.joglearth.opengl.GLContext;
import de.joglearth.source.caching.MemoryCache;


/**
 * Manages and displaces textures in OpenGL graphics memory.
 */
public class TexturePool<Key> extends MemoryCache<Key, Texture> {

    private GLContext gl;

    /**
     * Constructor.
     * @param gl The GL context holding the textures to manage. Must not be null
     */
    public TexturePool(GLContext gl) {
        if (gl == null) {
            throw new IllegalArgumentException();
        }
        
        this.gl = gl;
    }

    @Override
    public void dropObject(Key key) {
        //TODO System.err.println("TextureCache: dropping key " + key);
        final Texture value = super.requestObject(key, null).value;
        
        if (value != null) {
            gl.invokeSooner(new Runnable() {
                
                @Override
                public void run() {
                    gl.deleteTexture(value);
                }
            });
        }
        super.dropObject(key);
    }
}

