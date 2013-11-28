package de.joglearth;

import de.joglearth.geometry.Camera;
import de.joglearth.rendering.Renderer;
import de.joglearth.surface.LocationManager;
import de.joglearth.ui.MainWindow;


/**
 * Static main class of the application.
 * Provides application-specific constants and the main method.
 */
public final class JoglEarth {

    /**
     * The product title. Might be altered to mark milestones.
     */
    public static String PRODUCT_NAME = "Jogl Earth";

    /**
     * The current product version. Altered between releases.
     */
    public static String PRODUCT_VERSION = "0.1";

    /**
     * Initializes the JoglEarth application. There are no valid command line arguments.
     * 
     * @param args Command line arguments (unused)
     */
    public static void main(String[] args) {
        Camera camera = new Camera();
        LocationManager locationManager = new LocationManager();
        MainWindow gui = new MainWindow(locationManager, camera);
        Renderer r = new Renderer(gui.getGLCanvas(), locationManager, camera);
        gui.setVisible(true);
    }
    
}
