package de.joglearth.rendering;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.sun.rmi.rmid.ExecOptionPermission;

import de.joglearth.async.RunnableResultListener;
import de.joglearth.async.RunnableWithResult;
import de.joglearth.opengl.GLContext;
import de.joglearth.opengl.TextureFilter;
import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.SourceResponseType;


/**
 * Loads the textures into OpenGL returning the ID. Implements {@link Source} to get a new texture,
 * when it is needed. Owns a {@link Source} for image data.
 */
public class TextureLoader<Key> implements Source<Key, Texture> {

    private GLContext gl;
    private Source<Key, byte[]> imageSource;
    private ImageSourceListener imageSourceListener = new ImageSourceListener();
    private TextureFilter textureFilter;
    private String formatSuffix;
    private ExecutorService executor;

    // Stores requests requiring a callback on completion
    private Map<Key, Collection<SourceListener<Key, Texture>>> pendingRequests = new HashMap<>();


    /**
     * Constructor
     * 
     * @param gl The GL context to load textures with. Must not be null
     * @param imageSource The image source providing the raw image data. Must not be null
     * @param formatSuffix The file suffix of the image format provided (e.g. "jpg"). Must not be
     *        null
     */
    public TextureLoader(GLContext gl, Source<Key, byte[]> imageSource, String formatSuffix) {
        if (gl == null || imageSource == null || formatSuffix == null) {
            throw new IllegalArgumentException();
        }

        this.gl = gl;
        this.imageSource = imageSource;
        this.textureFilter = TextureFilter.TRILINEAR;
        this.formatSuffix = formatSuffix;
        
        this.executor = Executors.newSingleThreadExecutor();
    }


    private void loadTexture(final Key key, final SourceListener<Key, Texture> sender, byte[] raw) {

        final TextureData data;
        try {
            data = gl.loadTextureData(new ByteArrayInputStream(raw), formatSuffix);
        } catch (IOException e) {
            sender.requestCompleted(key, null);
            return;
        }

        gl.invokeLater(new RunnableWithResult() {

            @Override
            public Object run() {
                if (data == null) {
                    return null;
                }

                return gl.loadTexture(data, textureFilter);
            }

        }, new RunnableResultListener() {

            @Override
            public synchronized void runnableCompleted(Object result) {
                sender.requestCompleted(key, (Texture) result);
            }
        });
    }

    @Override
    public synchronized SourceResponse<Texture> requestObject(final Key key,
            final SourceListener<Key, Texture> sender) {

        if (key == null) {
            throw new IllegalArgumentException();
        }

        Collection<SourceListener<Key, Texture>> listeners = pendingRequests.get(key);
        if (listeners != null && listeners.size() != 0) {
            if (sender != null) {
                listeners.add(sender);
                return new SourceResponse<Texture>(SourceResponseType.ASYNCHRONOUS, null);
            } else {
                return new SourceResponse<Texture>(SourceResponseType.MISSING, null);
            }
            
        } else if (sender != null) {
            SourceResponse<byte[]> response = imageSource.requestObject(key, imageSourceListener);
            if (response.response != SourceResponseType.MISSING) {
                if (response.response == SourceResponseType.SYNCHRONOUS) {
                    final byte[] raw = response.value;
                    executor.execute(new Runnable() {

                        @Override
                        public void run() {
                            loadTexture(key, sender, raw);
                        }                        
                    });
                } else {
                    if (listeners == null) {
                        listeners = new LinkedList<>();
                        pendingRequests.put(key, listeners);
                    }
                    listeners.add(sender);
                }
                return new SourceResponse<Texture>(SourceResponseType.ASYNCHRONOUS, null);
            }
        }

        return new SourceResponse<Texture>(SourceResponseType.MISSING, null);
    }


    private class ImageSourceListener implements SourceListener<Key, byte[]> {

        @Override
        public void requestCompleted(Key key, byte[] value) {
            Collection<SourceListener<Key, Texture>> senders = pendingRequests.remove(key);

            if (senders != null && value != null) {
                for (SourceListener<Key, Texture> s : senders) {
                    loadTexture(key, s, value);
                }
            }
        }
    }


    @Override
    public void dispose() {}

    /**
     * Sets the texture filter used for the following textures.
     * 
     * @param textureFilter The filter. Must not be null
     */
    public void setTextureFilter(TextureFilter textureFilter) {
        if (textureFilter == null) {
            throw new IllegalArgumentException();
        }

        this.textureFilter = textureFilter;
    }

    /**
     * Sets the image source used to load image data from.
     * 
     * @param imageSource The source. Must not be null
     * @param formatSuffix The file suffix of the image format provided (e.g. "jpg"). Must not be
     *        null
     */
    public void setImageSource(Source<Key, byte[]> imageSource, String formatSuffix) {
        if (imageSource == null || formatSuffix == null) {
            throw new IllegalArgumentException();
        }

        this.imageSource = imageSource;
        this.formatSuffix = formatSuffix;
    }
}
