package de.joglearth.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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

    private static InputStream openResourceStream(String name) throws IOException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(name);
        if (url == null) {
            throw new IOException("Resource not found");
        } else {
            return url.openStream();
        }
    }

    /**
     * Loads a Swing <code>ImageIcon</code> from an image resource.
     * @param name The resource file name
     * @return The icon if successfully loaded, else <code>null</code>
     */
    public static ImageIcon loadIcon(String name) {
        try {
            return new ImageIcon(ImageIO.read(openResourceStream(name)));
        } catch (IOException e) {
            throw new RuntimeException("Loading resource " + name + " failed", e);
        }
    }

    public static TextureData loadTextureData(String name, String type) {
        try {
            return TextureIO.newTextureData(GLProfile.getDefault(), openResourceStream(name),
                    false, type);
        } catch (IOException e) {
            throw new RuntimeException("Loading resource " + name + " failed", e);
        }
    }

    public static Map<String, String> loadCSVMap(String name, String separatorRegex) {
        Map<String, String> map = new HashMap<>();
        try {
            InputStream resourceStream = openResourceStream(name);
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
}
