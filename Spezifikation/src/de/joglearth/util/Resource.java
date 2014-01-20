package de.joglearth.util;

import static javax.media.opengl.GL.GL_MAX_TEXTURE_SIZE;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.media.opengl.GL2;
import javax.media.opengl.GLProfile;
import javax.swing.ImageIcon;

import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;


/**
 * Utility class for loading data from JAR resources file.
 */
public final class Resource {

    private Resource() {}
    
    private static URL getURL(String name) {
        return Thread.currentThread().getContextClassLoader().getResource(name);
    }
    
    public static InputStream open(String name) throws IOException {
        //System.err.println("Opening "+name);
        URL url = getURL(name);
        if (url == null) {
            throw new IOException("Resource not found");
        } else {
            return url.openStream();
        }
    }
    
    public static boolean exists(String name) {
        return getURL(name) != null;
    }

    /**
     * Loads a Swing <code>ImageIcon</code> from an image resource.
     * 
     * @param name The resource file name
     * @return The icon if successfully loaded, else <code>null</code>
     */
    public static ImageIcon loadIcon(String name) {
        try {
            return new ImageIcon(ImageIO.read(open(name)));
        } catch (IOException e) {
            throw new RuntimeException("Loading resource " + name + " failed", e);
        }
    }

    public static Map<String, String> loadCSVMap(String name, String separatorRegex) {
        Map<String, String> map = new HashMap<>();
        try {
            InputStream resourceStream = open(name);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    resourceStream));
            
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(separatorRegex);
                if (parts.length == 2) {
                    map.put(parts[0], parts[1]);
                }
            }
            resourceStream.close();
        } catch (IOException e) {
            throw new RuntimeException("Loading resource " + name + " failed", e);
        }
        return map;
    }
    
    public static byte[] loadBinary(String name) {
        try {
            InputStream input = open(name);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buf = new byte[0x10000];
            int n;
            while ((n = input.read(buf)) != -1) {
                output.write(buf, 0, n);
            }
            return output.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Loading resource " + name + " failed", e);
        }
    }
}
