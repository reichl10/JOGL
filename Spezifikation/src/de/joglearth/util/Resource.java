package de.joglearth.util;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.opengl.GLProfile;
import javax.swing.ImageIcon;

import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;


/**
 * Utility class for loading data from JAR resources file.
 */
public final class Resource {

    private Resource() {}
    
    /**
     * Loads a Swing <code>ImageIcon</code> from an image resource.
     * 
     * @param name The resource file name
     * @return The icon if successfully loaded, else <code>null</code>
     */
    public static ImageIcon loadIcon(String name) {
        try {
            return new ImageIcon(ImageIO.read(Thread.currentThread()
                    .getContextClassLoader().getResource(name)));
        } catch (IOException e) {
            throw new RuntimeException("Loading resource " + name + " failed", e);
        }
    }

    public static TextureData loadTextureData(String name, String type) {
        try {
            return TextureIO.newTextureData(GLProfile.getDefault(), Thread.currentThread()
                    .getContextClassLoader().getResource(name), false, type);
        } catch (IOException e) {
            throw new RuntimeException("Loading resource " + name + " failed", e);
        }
    }
}
