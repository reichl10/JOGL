package de.joglearth.source.opengl;

import static javax.media.opengl.GL2.*;

import java.awt.Graphics2D;
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
import de.joglearth.rendering.Renderer;
import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.SourceResponseType;
import de.joglearth.util.AWTInvoker;
import de.joglearth.util.RunnableResultListener;
import de.joglearth.util.RunnableWithResult;


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
    private Renderer renderer;
    
    public TextureSource(Renderer renderer, GL2 gl, Source<Key, byte[]> imageSource) {
        this.renderer = renderer;
        this.gl = gl;
        this.imageSource = imageSource;
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
            
            Integer id = null;    
            BufferedImage image = null;
            try {
                image = ImageIO.read(new ByteArrayInputStream(raw));
            } catch (IOException e) {
                return null;
            }
            
            if (image.getData().getDataBuffer() instanceof DataBufferByte
                    && image.getType() == BufferedImage.TYPE_3BYTE_BGR) {
                byte[] imageData = ((DataBufferByte) image.getData().getDataBuffer()).getData();
                
                byte[] flippedImageData = new byte[imageData.length];
                int width = image.getWidth(), height = image.getHeight();
                for (int line = 0; line < height; ++line) {
                    System.arraycopy(imageData, line * width * 3, flippedImageData, (height-1-line) * width * 3, width * 3);
                }
    
                int[] ids = new int[1];
                gl.glGenTextures(1, ids, 0);
                GLError.throwIfActive(gl);
                id = ids[0];
                
                gl.glBindTexture(GL_TEXTURE_2D, id);
                GLError.throwIfActive(gl);
    
                gl.glTexImage2D(GL_TEXTURE_2D, 0, 3, image.getWidth(), image.getHeight(),
                        0, GL_BGR, GL_UNSIGNED_BYTE, ByteBuffer.wrap(flippedImageData));
                GLError.throwIfActive(gl);
                
                gl.glGenerateMipmap(GL_TEXTURE_2D);
                GLError.throwIfActive(gl);
                
                gl.glBindTexture(GL_TEXTURE_2D, 0);
                GLError.throwIfActive(gl);
                
                System.err.println("TextureSource: Loaded texture id " + id);
            } else {
                System.err.println("TextureSource: Texture format was not byte-wise BGR but " + image.getType() + ", returning null");
            }
            return new Integer(id);
        }
    
    }
    
    
    
    private void loadTexture(final Key key, final SourceListener<Key, Integer> sender, byte[] raw) {
        renderer.invokeLater(new TextureLoader(raw), new RunnableResultListener() {        
            @Override
            public synchronized void runnableCompleted(Object result) {
                sender.requestCompleted(key, (Integer) result);
            }
        });
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
                    loadTexture(key, sender, response.value);
                    return new SourceResponse<Integer>(SourceResponseType.ASYNCHRONOUS, null);
                    
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
                    loadTexture(key, s, value);
                }
            }
        }
    }
}
