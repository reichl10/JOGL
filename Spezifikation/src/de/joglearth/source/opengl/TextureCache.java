package de.joglearth.source.opengl;

import javax.media.opengl.GL2;

import de.joglearth.source.SourceResponse;
import de.joglearth.source.caching.MemoryCache;


/**
 * Manages and displaces textures in OpenGl graphics memory.
 */
public class TextureCache<Key> extends MemoryCache<Key, Integer> {

    private GL2 gl;
    
    public TextureCache(GL2 gl) {
        this.gl = gl;
    }

    @Override
    public void dropObject(Key key) {
        SourceResponse<Integer> superResponse = super.requestObject(key, null);
        if (superResponse.value != null) {
            gl.glDeleteTextures(1, new int[]{superResponse.value}, 0);
        }
        super.dropObject(key);
    }

}

