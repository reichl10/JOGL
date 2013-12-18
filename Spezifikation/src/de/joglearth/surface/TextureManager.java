package de.joglearth.surface;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import de.joglearth.geometry.Tile;
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
    private Integer placeholderTextureId;
    private List<SurfaceListener> listeners = new ArrayList<>();
    private RequestDistributor<OSMTile, Integer> dist;
    private TiledMapType mapType = TiledMapType.OSM_MAPNIK;
    private TextureListener textureListener = new TextureListener();
    
    
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
    

    /**
     * Constructor.
     * 
     * @param gl The OpenGL object
     */
    public TextureManager(GL2 gl, Source<OSMTile, byte[]> imageSource, int textureCacheSize) {
        dist = new RequestDistributor<>();
        dist.addCache(new TextureCache<OSMTile>(gl), textureCacheSize);
        dist.setSource(new TextureSource<>(gl, imageSource));
        
        placeholderTexture = TextureIO.newTexture(Resource.loadTextureData(
                "textures/placeholder.png", "png"));
        placeholderTextureId = new Integer(placeholderTexture.getTextureObject(gl));
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
        return textureId != null ? textureId : placeholderTextureId;
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
