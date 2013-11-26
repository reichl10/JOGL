package de.joglearth.util;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


/**
 * Utility class for loading data from JAR resources file.
 */
public final class Resource {

    private Resource() {}

    /**
     * Loads a Swing <code>ImageIcon</code> from an image resource.
     * @param name The resource file name.
     * @return The icon if successfully loaded, else <code>null</code>.
     */
    public static ImageIcon loadIcon(String name) {
        try {
            return new ImageIcon(ImageIO.read(Thread.currentThread()
                    .getContextClassLoader().getResource(name)));
        } catch (IOException e) {
            return null;
        }
    }
}
