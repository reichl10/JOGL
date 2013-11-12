package de.joglearth.ui;


import de.joglearth.geometry.Camera;
import de.joglearth.geometry.CameraListener;
import de.joglearth.geometry.Tile;
import de.joglearth.surface.SurfaceListener;
import de.joglearth.surface.LocationManager;
import de.joglearth.settings.Settings;
import de.joglearth.settings.SettingsListener;

import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;


public class GUI extends JFrame implements SurfaceListener, CameraListener, SettingsListener {
	/**
	 * makes the compiler happy
	 */
	private static final long serialVersionUID = -7540009258222187987L;
	
	private final int MIN_WIDTH = 800;
	private final int MIN_HEIGTH = 600;
	private int height;
	private int width;
	private LocationManager locationManager;
	private Settings settings;
	private ViewEventListener viewEventListener;
	private GUIEventListener guiEventListener;
	private Camera camera;
	
	public GUI(LocationManager locationManager, Camera camera) {
		this.locationManager = locationManager;
		this.settings = Settings.getInstance();
		this.viewEventListener = new ViewEventListener(camera);
		this.guiEventListener = new GUIEventListener(camera);
	}
	
	public GLCanvas getGLCanvas() {
		return null;
	}

	@Override
	public void settingsChanged(String key, Object valOld, Object valNew) {
		// TODO Automatisch erstellter Methoden-Stub
		
	}

	@Override
	public void surfaceChanged(Tile tile) {
		// TODO Automatisch erstellter Methoden-Stub
		
	}
}