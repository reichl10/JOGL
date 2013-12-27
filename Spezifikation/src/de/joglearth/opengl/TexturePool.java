package de.joglearth.opengl;

import com.jogamp.opengl.util.texture.Texture;

import de.joglearth.source.caching.MemoryCache;


/**
 * Manages and displaces textures in OpenGl graphics memory.
 */
public class TexturePool<Key> extends MemoryCache<Key, Texture> {

    private GLContext gl;

    public TexturePool(GLContext gl) {
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

