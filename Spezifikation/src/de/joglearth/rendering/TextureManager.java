package de.joglearth.rendering;

import java.util.ArrayList;
import java.util.List;

import static javax.media.opengl.GL2.*;

import com.jogamp.opengl.util.texture.Texture;

import de.joglearth.geometry.Matrix4;
import de.joglearth.geometry.SurfaceListener;
import de.joglearth.geometry.Tile;
import de.joglearth.geometry.TransformedTile;
import de.joglearth.map.MapConfiguration;
import de.joglearth.map.TileName;
import de.joglearth.opengl.GLContext;
import de.joglearth.opengl.TextureFilter;
import de.joglearth.opengl.TransformedTexture;
import de.joglearth.settings.Settings;
import de.joglearth.settings.SettingsContract;
import de.joglearth.settings.SettingsListener;
import de.joglearth.source.Priorized;
import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.caching.RequestDistributor;


/**
 * Executes requests for textures of the {@link Renderer}. Loads textures from a
 * {@link RequestDistrubutor} which accesses a {@link TextureLoader} and a {@link TexturePool}.
 */
public class TextureManager implements Priorized {

    private Texture placeholder;
    private List<SurfaceListener> listeners = new ArrayList<>();
    private RequestDistributor<TileName, Texture> dist;
    private TextureListener textureListener = new TextureListener();
    private GLContext gl;
    private volatile TextureFilter textureFilter;
    private TextureLoader<TileName> textureSource;
    private TextureSettingsListener settingsListener;
    private MapConfiguration mapConfiguration;
    private Source<TileName, byte[]> imageSource;


    /**
     * Constructor.
     * 
     * @param gl The OpenGL object
     * @param textureCacheSize The size of the texture cache
     * @param configuration The configuration of the map
     */
    public TextureManager(GLContext gl, int textureCacheSize, MapConfiguration configuration) {
        this.gl = gl;
        this.mapConfiguration = configuration;
        this.imageSource = configuration.getImageSource();
        this.textureSource = new TextureLoader<TileName>(gl, imageSource, 
                configuration.getImageFormatSuffix());

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
     * Is called if a texture of a {@link Tile} should be loaded.
     * 
     * @param tile The <code>Tile</code> that should be loaded
     * @return Returns a loaded OpenGl identifier for the texture or if it is not yet loaded, the
     *         method returns a place holder texture
     */
    public synchronized TransformedTexture getTexture(Tile tile, int scaleDown) {
        if (tile == null || scaleDown < 0) {
            throw new IllegalArgumentException();
        }
        
        Matrix4 transformation = Matrix4.IDENTITY;
        while (scaleDown > 0) {
            TransformedTile scaledTile = tile.getScaledAlternative();
            if (scaledTile != null) {
                tile = scaledTile.tile;
                transformation = scaledTile.transformation.multiply(transformation);
            } else {
                break;
            }
            --scaleDown;
        }
        
        while (tile != null) {
            TileName key = new TileName(mapConfiguration, tile);
            SourceResponse<Texture> response = dist.requestObject(
                    key, textureListener);
            Texture textureId = response.value;

            if (textureId != null) {
                return new TransformedTexture(textureId, transformation);
            } else {
                TransformedTile alternative = tile.getScaledAlternative();
                if (alternative != null) {
                    tile = alternative.tile;
                    transformation = alternative.transformation.multiply(transformation);
                } else {
                    tile = null;
                }
            }
        }
        return new TransformedTexture(placeholder, Matrix4.IDENTITY);
    }

    /**
     * Sets the configuration of the map.
     * 
     * @param configuration The configuration of the map
     */
    public synchronized void setMapConfiguration(MapConfiguration configuration) {
        mapConfiguration = configuration;
        this.imageSource = configuration.getImageSource();
        textureSource.setImageSource(imageSource, configuration.getImageFormatSuffix());
    }

    /**
     * Adds a {@link SurfaceListener} that distributes a notification if the surface was changed.
     * 
     * @param listener The new listener
     */
    public synchronized void addSurfaceListener(SurfaceListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a specific {@link SurfaceListener}.
     * 
     * @param listener The listener that should be removed
     */
    public synchronized void removeSurfaceListener(SurfaceListener listener) {
        while (listeners.remove(listener)){}
    }

    /**
     *  Called if the program has been closed to terminate all pending processes.
     */
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

        return gl.loadTexture(image, blocks * pixelsPerBlock, blocks * pixelsPerBlock, GL_RGB,
                GL_RGB,
                textureFilter);
    }
    

    @Override
    public void increasePriority() {
        textureSource.increasePriority();
        if (imageSource instanceof Priorized) {
            ((Priorized) imageSource).increasePriority();
        }
    }
}
