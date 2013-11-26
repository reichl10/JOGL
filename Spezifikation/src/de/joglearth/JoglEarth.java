package de.joglearth;

import de.joglearth.geometry.Camera;
import de.joglearth.rendering.Renderer;
import de.joglearth.surface.LocationManager;
import de.joglearth.ui.MainWindow;


/**
 * Utility Class that contains the main method of JoglEarth.
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
     * Initializes the JoglEarth Application. There are no valid command line arguments.
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Camera camera = new Camera();
        LocationManager locationManager = new LocationManager();
        MainWindow gui = new MainWindow(locationManager, camera);
        Renderer r = new Renderer(gui.getGLCanvas(), locationManager, camera);
        gui.setVisible(true);
    }
}
