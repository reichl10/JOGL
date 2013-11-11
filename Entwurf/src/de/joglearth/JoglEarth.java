package de.joglearth;

import de.joglearth.geometry.Camera;
import de.joglearth.geometry.HeightMap;
import de.joglearth.geometry.Camera.Type;
import de.joglearth.location.LocationManager;
import de.joglearth.rendering.*;
import de.joglearth.settings.Settings;
import de.joglearth.source.*;
import de.joglearth.ui.*;

public final class JoglEarth {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Camera camera = new Camera(Camera.Type.SPHERE);
		LocationManager lm = new LocationManager(settings);
		GUI gui = new GUI(lm, settings, camera);
		HeightMap height = new HeightMap();
		Renderer r = new Renderer(gui.getGLCanvas(), height ,lm, camera);
		gui.addUpdateListener(r);
		gui.setVisible(true);
	}
}
