package de.joglearth.view;

import de.joglearth.model.*;
import de.joglearth.view.*;
import de.joglearth.controller.*;

import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

public class GUI extends JFrame implements UpdateListener {
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
	
	public GUI(LocationManager locationManager, Settings settings,
			Camera camera) {
		this.locationManager = locationManager;
		locationManager.addUpdateListener(this);
		this.settings = settings;
		this.viewEventListener = new ViewEventListener(camera);
		this.guiEventListener = new GUIEventListener(camera);
	}

	public void addUpdateListener(UpdateListener listener) {
		viewEventListener.addUpdateListener(listener);
		guiEventListener.addUpdateListener(listener);
	}
	
	public GLCanvas getGLCanvas() {
		return null;
	}
	
	@Override
	public void post() {
		
	}
}