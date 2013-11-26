package de.joglearth.util;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


public final class Resource {

    public static ImageIcon loadIcon(String name) {
        try {
            return new ImageIcon(ImageIO.read(Thread.currentThread()
                    .getContextClassLoader().getResource(name)));
        } catch (IOException e) {
            return null;
        }
    }
}
