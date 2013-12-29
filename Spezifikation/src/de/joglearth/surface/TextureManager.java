package de.joglearth.surface;

import java.util.ArrayList;
import java.util.List;

import static javax.media.opengl.GL2.*;

import com.jogamp.opengl.util.texture.Texture;

import de.joglearth.geometry.Tile;
import de.joglearth.map.MapConfiguration;
import de.joglearth.map.TileName;
import de.joglearth.opengl.GLContext;
import de.joglearth.opengl.TexturePool;
import de.joglearth.opengl.TextureFilter;
import de.joglearth.opengl.TextureLoader;
import de.joglearth.rendering.Renderer;
import de.joglearth.settings.Settings;
import de.joglearth.settings.SettingsContract;
import de.joglearth.settings.SettingsListener;
import de.joglearth.source.SourceListener;
import de.joglearth.source.caching.RequestDistributor;

/**
 * Executes requests for textures of the {@link Renderer}. Loads textures from a
 * {@link de.joglearth.source.caching.RequestDistrubutor} which accesses a
 * {@link de.joglearth.opengl.TextureLoader} and a
 * {@link de.joglearth.opengl.TexturePool}.
 */
public class TextureManager {
    
    private Texture placeholder;
    private List<SurfaceListener> listeners = new ArrayList<>();
    private RequestDistributor<TileName, Texture> dist;
    private TextureListener textureListener = new TextureListener();
    private GLContext gl;
    private volatile TextureFilter textureFilter;
    private TextureLoader<TileName> textureSource;
    private TextureSettingsListener settingsListener;
    private MapConfiguration mapConfiguration;
    
    
    private class TextureSettingsListener implements SettingsListener {

        @Override
        public void settingsChanged(String key, Object valOld, Object valNew) {
            if (key.equals(SettingsContract.TEXTURE_FILTER)) {
                dist.dropAll();
                gl.invokeSooner(new Runnable() {
                    
                    @Override
                    public void run() {
                        gl.deleteTexture(placeholder);
                    }
                });
                initialize();
            }
        }
    }
    
    
    private void notifyListeners(Tile tile) {
        for (SurfaceListener l : listeners) {
            l.surfaceChanged(tile.getLongitudeFrom(), tile.getLatitudeFrom(),
                    tile.getLongitudeTo(), tile.getLatitudeTo());
        }
    }
    
    
    private class TextureListener implements SourceListener<TileName, Texture> {

        @Override
        public void requestCompleted(TileName key, Texture value) {
            notifyListeners(key.tile);
        }
    }
    
    
    private Texture loadCheckerboardTexture(int blocks, int pixelsPerBlock) {
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

        return gl.loadTexture(image, blocks*pixelsPerBlock, blocks*pixelsPerBlock, GL_RGB, GL_RGB,
                textureFilter);
    }
    

    /**
     * Constructor.
     * 
     * @param gl The OpenGL object
     */
    public TextureManager(GLContext gl, int textureCacheSize, MapConfiguration configuration) {
        this.gl = gl;
        this.mapConfiguration = configuration;
        this.textureSource = new TextureLoader<TileName>(gl, 
                configuration.getImageSource(), configuration.getImageFormatSuffix());

        dist = new RequestDistributor<>();
        dist.addCache(new TexturePool<TileName>(gl), textureCacheSize);
        dist.setSource(textureSource);

        settingsListener = new TextureSettingsListener();
        Settings.getInstance().addSettingsListener(SettingsContract.TEXTURE_FILTER,
                settingsListener);

        initialize();
    }
    
    
    private void initialize() {
        textureFilter = TextureFilter.valueOf(Settings.getInstance().getString(
                SettingsContract.TEXTURE_FILTER));
        textureSource.setTextureFilter(textureFilter);
        gl.invokeSooner(new Runnable() {

            @Override
            public void run() {
                placeholder = loadCheckerboardTexture(16, 32);
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
    public synchronized Texture getTexture(Tile tile) {
        //TODO System.err.println("TextureManager: requesting texture for " + tile);
        Texture textureId = dist.requestObject(new TileName(mapConfiguration, tile), textureListener).value;
        //TODO System.err.println("TextureManager: returning "
        //        + (textureId == null ? "placeholder" : "real texture") + " for " + tile);
        return textureId != null ? textureId : placeholder;
    }
    
    
    public synchronized void setMapConfiguration(MapConfiguration configuration) {
        mapConfiguration = configuration;
        textureSource.setImageSource(configuration.getImageSource(),
                configuration.getImageFormatSuffix());
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
    
    
    public void dispose() {
        dist.dispose();
        
        gl.invokeSooner(new Runnable() {
            
            @Override
            public void run() {
                gl.deleteTexture(placeholder);
            }
        });

        Settings.getInstance().removeSettingsListener(SettingsContract.TEXTURE_FILTER,
                settingsListener);
    }
}
