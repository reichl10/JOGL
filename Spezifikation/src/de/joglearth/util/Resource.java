package de.joglearth.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


/**
 * Utility class for loading data from JAR resources file.
 */
public final class Resource {

    private Resource() {}
    
    private static URL getURL(String name) {
        return Thread.currentThread().getContextClassLoader().getResource(name);
    }
    
    /**
     * Reads an URL given as an input String and opens the download stream.
     * 
     * @param name The URL in String representation
     * @return The InputStream opened with the URL
     * @throws IOException If the name can't be resolved to a valid URL
     */
    public static InputStream open(String name) throws IOException {
        URL url = getURL(name);
        if (url == null) {
            throw new IOException("Resource not found");
        } else {
            return url.openStream();
        }
    }
    
    /**
     * Checks if the URL to a given name exists.
     * 
     * @param name The name of the URL to check
     * @return true, if the URL exists, false if not
     */
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
            InputStream input = open(name);
            try {
                return new ImageIcon(ImageIO.read(input));
            } finally {
                input.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Loading resource " + name + " failed", e);
        }
    }

    /**
     * Loads a CSVMap that contains a certain name and opens a Stream to load the right part
     * of the CSVMap.
     * 
     * @param name The name to look for in the map
     * @param separatorRegex The separator of the values
     * @return A map containing the searched values
     */
    public static Map<String, String> loadCSVMap(String name, String separatorRegex) {
        Map<String, String> map = new HashMap<>();
        try {
            InputStream resourceStream = open(name);
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        resourceStream));
                try {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(separatorRegex);
                        if (parts.length == 2) {
                            map.put(parts[0], parts[1]);
                        }
                    }
                } finally {
                    reader.close();
                }
            } finally {
                resourceStream.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Loading resource " + name + " failed", e);
        }
        return map;
    }
    
    /**
     * Loads a binary file from a given source.
     * 
     * @param name The String representation of the source
     * @return A byte array containing the wanted binary.
     */
    public static byte[] loadBinary(String name) {
        try {
            InputStream input = open(name);
            try {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                byte[] buf = new byte[0x10000];
                int n;
                while ((n = input.read(buf)) != -1) {
                    output.write(buf, 0, n);
                }
                return output.toByteArray();
            } finally {
                input.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Loading resource " + name + " failed", e);
        }
    }
    
    public static String loadText(String name) {
        try {
            InputStream input = open(name);
            try {
                return new Scanner(input).useDelimiter("\\Z").next();
            } finally {
                input.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Loading resource " + name + " failed", e);
        }
    }
}
