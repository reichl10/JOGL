package de.joglearth;

import de.joglearth.geometry.Camera;
import de.joglearth.geometry.HeightMap;
import de.joglearth.surface.LocationManager;
import de.joglearth.rendering.*;
import de.joglearth.ui.*;

public final class JoglEarth {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Camera camera = new Camera();
		LocationManager lm = new LocationManager();
		GUI gui = new GUI(lm, camera);
		HeightMap height = new HeightMap();
		Renderer r = new Renderer(gui.getGLCanvas(), height ,lm, camera);
		gui.setVisible(true);
	}
}
