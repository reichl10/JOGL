package de.joglearth.source.opengl;

import com.jogamp.opengl.util.texture.Texture;

import de.joglearth.opengl.GLContext;
import de.joglearth.source.caching.MemoryCache;


/**
 * Manages and displaces textures in OpenGl graphics memory.
 */
public class TextureCache<Key> extends MemoryCache<Key, Texture> {

    private GLContext gl;

    public TextureCache(GLContext gl) {
        this.gl = gl;
    }

    @Override
    public void dropObject(Key key) {
        System.err.println("TextureCache: dropping key " + key);
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

