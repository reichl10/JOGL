package de.joglearth;

import de.joglearth.geometry.Camera;
import de.joglearth.surface.HeightMap;
import de.joglearth.surface.LocationManager;
import de.joglearth.rendering.*;
import de.joglearth.ui.*;

/**
 * Utility Class that contains the main method of JoglEarth.
 */
public final class JoglEarth {

    public static String PRODUCT_NAME = "Jogl Earth";
    public static String PRODUCT_VERSION = "0.1";
    
	/**
	 * Initializes the JoglEarth Application.
	 * There are no valid command line arguments.
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
