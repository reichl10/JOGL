package de.joglearth.source.opengl;

import static javax.media.opengl.GL2.*;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.media.opengl.GL2;

import de.joglearth.rendering.GLError;
import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.SourceResponseType;


/**
 * Loads the textures into OpenGL returning the ID. Implements {@link de.joglearth.source.Source} 
 * to get a new texture,
 * when it is needed. Owns a {@link de.joglearth.source.Source} for image data.
 */
public class TextureSource<Key> implements Source<Key, Integer> {

    private GL2                  gl;
    private Source<Key, byte[]> imageSource;
    private ImageSourceListener imageSourceListener = new ImageSourceListener();
    private Map<Key, Collection<SourceListener<Key, Integer>>> pendingRequests = new HashMap<>();

    
    public TextureSource(GL2 gl, Source<Key, byte[]> imageSource) {
        this.gl = gl;
        this.imageSource = imageSource;
    }
    
    
    private Integer loadTexture(byte[] raw) {
        if (raw == null) {
            return null;
        }
        
        Integer id = null;    
        BufferedImage image = null;
        try {
            image = ImageIO.read(new ByteArrayInputStream(raw));
        } catch (IOException e) {
            return null;
        }
        
        if (image.getData().getDataBuffer() instanceof DataBufferByte
                && image.getType() == BufferedImage.TYPE_4BYTE_ABGR) {
            byte[] imageData = ((DataBufferByte) image.getData().getDataBuffer()).getData();

            int[] ids = new int[1];
            gl.glGenTextures(1, ids, 0);
            GLError.throwIfActive(gl);
            id = ids[0];
            
            gl.glBindTexture(GL_TEXTURE_2D, id);
            GLError.throwIfActive(gl);

            gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_ABGR_EXT, image.getWidth(), image.getHeight(),
                    0, GL_ABGR_EXT, GL_UNSIGNED_BYTE, ByteBuffer.wrap(imageData));
            GLError.throwIfActive(gl);
            
            gl.glGenerateMipmap(GL_TEXTURE_2D);
            GLError.throwIfActive(gl);
            
            gl.glBindTexture(GL_TEXTURE_2D, 0);
            GLError.throwIfActive(gl);
        } else {
            System.err.println("Texture format was not byte-wise ABGR, returning null");
        }
    
        return id;
    }

    @Override
    public synchronized SourceResponse<Integer> requestObject(Key key,
            SourceListener<Key, Integer> sender) {
        
        if (key == null) {
            throw new IllegalArgumentException();
        }
        
        System.err.println("TextureSource: requested " + key);

        Collection<SourceListener<Key, Integer>> listeners = pendingRequests.get(key);
        if (listeners != null && listeners.size() != 0) {
            if (sender != null) {
                listeners.add(sender);
                return new SourceResponse<Integer>(SourceResponseType.ASYNCHRONOUS, null);
            } else {
                return new SourceResponse<Integer>(SourceResponseType.MISSING, null);
            }
        } else  {
            SourceResponse<byte[]> response = imageSource.requestObject(key, imageSourceListener);
            switch (response.response) {                
                case SYNCHRONOUS:
                    return new SourceResponse<Integer>(SourceResponseType.SYNCHRONOUS, 
                            loadTexture(response.value));
                    
                case ASYNCHRONOUS: 
                    if (sender != null) {
                        if (listeners == null) {
                            listeners = new LinkedList<>();
                            pendingRequests.put(key, listeners);
                        }
                        listeners.add(sender);
                        return new SourceResponse<Integer>(SourceResponseType.ASYNCHRONOUS, null);
                    }
                    // else fall through
                            
                default:
                    return new SourceResponse<Integer>(SourceResponseType.MISSING, null);
            }
        }
    }

    private class ImageSourceListener implements SourceListener<Key, byte[]> {
        @Override
        public synchronized void requestCompleted(Key key, byte[] value) {
            Collection<SourceListener<Key, Integer>> senders = pendingRequests.remove(key);
            
            if (senders != null) {
                for (SourceListener<Key, Integer> s : senders) {
                    System.err.println("TextureSource: loading texture for " + key);
                    s.requestCompleted(key, loadTexture(value));
                    System.err.println("TextureSource: done loading texture for " + key);
                }
            }
        }
    }
}
