package de.joglearth.source.opengl;

import javax.media.opengl.GL2;

import de.joglearth.rendering.GLError;
import de.joglearth.rendering.Renderer;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.caching.MemoryCache;


/**
 * Manages and displaces textures in OpenGl graphics memory.
 */
public class TextureCache<Key> extends MemoryCache<Key, Integer> {

    private GL2 gl;
    private Renderer renderer;
    
    public TextureCache(Renderer renderer, GL2 gl) {
        this.renderer = renderer;
        this.gl = gl;
    }

    @Override
    public void dropObject(Key key) {
        System.err.println("TextureCache: dropping key " + key);
        final SourceResponse<Integer> superResponse = super.requestObject(key, null);
        
        if (superResponse.value != null) {
            Runnable deleter = new Runnable() {
                @Override
                public void run() {
                    gl.glDeleteTextures(1, new int[]{superResponse.value}, 0);
                    GLError.throwIfActive(gl);
                }
            };
            
            if (renderer.isInsideDisplayFunction()) {
                deleter.run();
            } else {
                renderer.invokeLater(deleter);
            }
        }
        super.dropObject(key);
    }

}

