package de.joglearth.controller;

import de.joglearth.model.*;
import de.joglearth.view.*;
import de.joglearth.controller.*;

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