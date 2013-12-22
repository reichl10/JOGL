package de.joglearth.surface;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import static javax.media.opengl.GL.GL_LINEAR;
import static javax.media.opengl.GL.GL_LINEAR_MIPMAP_LINEAR;
import static javax.media.opengl.GL.GL_TEXTURE_2D;
import static javax.media.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static javax.media.opengl.GL.GL_TEXTURE_MIN_FILTER;
import static javax.media.opengl.GL2.*;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import de.joglearth.geometry.Tile;
import de.joglearth.opengl.GLContext;
import de.joglearth.opengl.GLError;
import de.joglearth.rendering.Renderer;
import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.caching.RequestDistributor;
import de.joglearth.source.opengl.TextureCache;
import de.joglearth.source.opengl.TextureSource;
import de.joglearth.source.osm.OSMTile;
import de.joglearth.util.Resource;

/**
 * Executes requests for textures of the {@link Renderer}. Loads textures from a
 * {@link de.joglearth.source.caching.RequestDistrubutor} which accesses a
 * {@link de.joglearth.source.opengl.TextureSource} and a
 * {@link de.joglearth.source.opengl.TextureCache}.
 */
public class TextureManager {

    private Texture placeholderTexture;
    private Integer placeholder;
    private List<SurfaceListener> listeners = new ArrayList<>();
    private RequestDistributor<OSMTile, Integer> dist;
    private TiledMapType mapType = TiledMapType.OSM_MAPNIK;
    private TextureListener textureListener = new TextureListener();
    private GLContext gl;
    
    
    private void notifyListeners(Tile tile) {
        for (SurfaceListener l : listeners) {
            l.surfaceChanged(tile.getLongitudeFrom(), tile.getLatitudeFrom(),
                    tile.getLongitudeTo(), tile.getLatitudeTo());
        }
    }
    
    
    private class TextureListener implements SourceListener<OSMTile, Integer> {

        @Override
        public void requestCompleted(OSMTile key, Integer value) {
            notifyListeners(key.tile);
        }
    }
    
    
    private int loadChessBoardTexture(int blocks, int pixelsPerBlock) {
        byte[] fullLine = new byte[(blocks + 1) * pixelsPerBlock * 3];
        for (int col = 0; col < blocks + 1; ++col) {
            for (int pixel = 0; pixel < pixelsPerBlock; ++pixel) {
                 byte greyValue = (byte) ((col % 2 == 0) ? 128 : 255);
                 for (int component = 0; component < 3; ++component) {
                     fullLine[3 * (col * pixelsPerBlock + pixel) + component] = greyValue;
                 }
            }
        }

        byte[] image = new byte[3 * (blocks * pixelsPerBlock) * (blocks * pixelsPerBlock)];
        for (int line = 0; line < blocks; ++line) {
            for (int pixelLine = 0; pixelLine < pixelsPerBlock; ++pixelLine) {
                System.arraycopy(fullLine, 3 * ((line % 2 == 0) ? 0 : pixelsPerBlock), image, 
                        3 * (line * pixelsPerBlock + pixelLine) * blocks * pixelsPerBlock,
                        3 * blocks * pixelsPerBlock);
            }
        }

        return gl.loadTexture(image, GL_RGB, GL_RGB, blocks * pixelsPerBlock, blocks
                * pixelsPerBlock, false);
    }
    

    /**
     * Constructor.
     * 
     * @param gl The OpenGL object
     */
    public TextureManager(GLContext gl, Source<OSMTile, byte[]> imageSource,
            int textureCacheSize) {
        this.gl = gl;

        dist = new RequestDistributor<>();
        dist.addCache(new TextureCache<OSMTile>(gl), textureCacheSize);
        dist.setSource(new TextureSource<>(gl, imageSource));
        
        gl.invokeSooner(new Runnable() {

            @Override
            public void run() {
                placeholder = loadChessBoardTexture(16, 32);
            }
        });
    }

    /**
     * Is called if a texture of a {@link de.joglearth.geometry.Tile} should be loaded.
     * 
     * @param tile The <code>Tile</code> that should be loaded
     * @return Returns a loaded OpenGl identifier for the texture or if it is not yet loaded, the
     *         method returns a place holder texture
     */
    public synchronized Integer getTexture(Tile tile) {
        System.err.println("TextureManager: requesting texture for " + tile);
        Integer textureId = dist.requestObject(new OSMTile(tile, mapType), textureListener).value;
        System.err.println("TextureManager: returning "
                + (textureId == null ? "placeholder" : "real texture") + " for " + tile);
        return textureId != null ? textureId : placeholder;
    }
    
    
    public synchronized void setMapType(TiledMapType t) {
        mapType = t;
    }

    /**
     * Adds a {@link SurfaceListener} that distributes a notification if the
     * surface was changed.
     * 
     * @param l
     *            The new listener
     */
    public synchronized void addSurfaceListener(SurfaceListener l) {
        listeners.add(l);
    }

    /**
     * Removes a specific {@link SurfaceListener}.
     * 
     * @param l
     *            The listener that should be removed
     */
    public synchronized void removeSurfaceListener(SurfaceListener l) {
        while(listeners.remove(l));
    }
}
