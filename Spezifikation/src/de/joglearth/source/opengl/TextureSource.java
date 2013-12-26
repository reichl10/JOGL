package de.joglearth.source.opengl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.jogamp.opengl.util.texture.Texture;

import de.joglearth.async.RunnableResultListener;
import de.joglearth.async.RunnableWithResult;
import de.joglearth.opengl.GLContext;
import de.joglearth.opengl.TextureFilter;
import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.SourceResponseType;
import de.joglearth.source.TileName;


/**
 * Loads the textures into OpenGL returning the ID. Implements {@link de.joglearth.source.Source} to
 * get a new texture, when it is needed. Owns a {@link de.joglearth.source.Source} for image data.
 */
public class TextureSource<Key> implements Source<Key, Texture> {

    private GLContext gl;
    private Source<Key, byte[]> imageSource;
    private ImageSourceListener imageSourceListener = new ImageSourceListener();
    private Map<Key, Collection<SourceListener<Key, Texture>>> pendingRequests = new HashMap<>();
    private TextureFilter textureFilter;


    public TextureSource(GLContext gl, Source<Key, byte[]> imageSource) {
        this.gl = gl;
        this.imageSource = imageSource;
        this.textureFilter = TextureFilter.NEAREST;
    }


    private class TextureLoader implements RunnableWithResult {

        private byte[] raw;


        public TextureLoader(byte[] raw) {
            this.raw = raw;
        }

        @Override
        public Object run() {
            if (raw == null) {
                return null;
            }

            try {
                return gl.loadTexture(new ByteArrayInputStream(raw), "jpg", textureFilter);
            } catch (IOException e) {
                return null;
            }
        }

    }


    private void loadTexture(final Key key, final SourceListener<Key, Texture> sender, byte[] raw) {
        gl.invokeLater(new TextureLoader(raw), new RunnableResultListener() {

            @Override
            public synchronized void runnableCompleted(Object result) {
                sender.requestCompleted(key, (Texture) result);
            }
        });
    }

    @Override
    public synchronized SourceResponse<Texture> requestObject(Key key,
            SourceListener<Key, Texture> sender) {

        if (key == null) {
            throw new IllegalArgumentException();
        }

        //TODO System.err.println("TextureSource: requested " + key);

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
                    loadTexture(key, sender, response.value);
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

            if (senders != null) {
                for (SourceListener<Key, Texture> s : senders) {
                    //TODO System.err.println("TextureSource: loading texture for " + key);
                    loadTexture(key, s, value);
                }
            }
        }
    }


    @Override
    public void dispose() { }

    public void setTextureFilter(TextureFilter textureFilter) {
        this.textureFilter = textureFilter;
    }

    public void setImageSource(Source<Key, byte[]> imageSource) {
        this.imageSource = imageSource;
    }
}
