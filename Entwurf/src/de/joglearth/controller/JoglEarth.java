package de.joglearth.controller;

import de.joglearth.model.LocationManager;
import de.joglearth.model.Settings;
import de.joglearth.view.GUI;
import de.joglearth.view.Renderer;

public class JoglEarth {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Settings settings = new Settings();
		Camera camera = new Camera(Camera.Type.SPHERE);
		LocationManager lm = new LocationManager(settings);
		GUI gui = new GUI(lm, settings, camera);
		HeightMap height = new HeightMap();
		Renderer r = new Renderer(gui.getGLCanvas(), height ,lm, camera);
		gui.addUpdateListener(r);
		gui.setVisible(true);
	}
}