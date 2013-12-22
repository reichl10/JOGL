package de.joglearth.source.opengl;

import de.joglearth.opengl.GLContext;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.caching.MemoryCache;


/**
 * Manages and displaces textures in OpenGl graphics memory.
 */
public class TextureCache<Key> extends MemoryCache<Key, Integer> {

    private GLContext gl;

    public TextureCache(GLContext gl) {
        this.gl = gl;
    }

    @Override
    public void dropObject(Key key) {
        System.err.println("TextureCache: dropping key " + key);
        final SourceResponse<Integer> superResponse = super.requestObject(key, null);
        
        if (superResponse.value != null) {
            gl.invokeSooner(new Runnable() {
                
                @Override
                public void run() {
                    gl.deleteTexture(superResponse.value);
                }
            });
        }
        super.dropObject(key);
    }

}

