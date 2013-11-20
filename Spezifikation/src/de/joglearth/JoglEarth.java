package de.joglearth;

import de.joglearth.geometry.Camera;
import de.joglearth.surface.HeightMapManager;
import de.joglearth.surface.LocationManager;
import de.joglearth.rendering.*;
import de.joglearth.ui.*;

/**
 * Utility Class that contains the main method of JoglEarth.
 */
public final class JoglEarth {

	/**
	 * Initializes the JoglEarth Application.
	 * There are no valid command line arguments.
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		Camera camera = new Camera();
		LocationManager lm = new LocationManager();
		MainWindow gui = new MainWindow(lm, camera);
		HeightMapManager height = new HeightMapManager();
		Renderer r = new Renderer(gui.getGLCanvas(), height ,lm, camera);
		gui.setVisible(true);
	}
}
