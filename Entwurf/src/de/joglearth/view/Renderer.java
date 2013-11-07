package de.joglearth.view;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;

import de.joglearth.model.*;
import de.joglearth.view.*;
import de.joglearth.controller.*;


public class Renderer implements RequestListener<Tile, Integer>, Runnable,
		UpdateListener {

	private GL2 gl;
	private boolean quit = false;
	private boolean running;
	private boolean posted;
	private HeightMap height;
	private LocationManager locationManager;
	private TextureCache textureCache;
	private Camera camera;
	
	public Renderer(GLCanvas canv, HeightMap height,
			LocationManager locationManager, Camera camera) {
		this.locationManager = locationManager;
		this.camera = camera;
		this.gl = canv.getGL().getGL2();
		canv.addGLEventListener(new RendererEventListener());
		FileSystemCache<TileKey, byte[]> fsCache = null;
		HTTPSource<TileKey, byte[]> source = null;
		memCache = new MemoryCache<TileKey, byte[]>(new CacheListener(), fsCache,
				source);
		textureCache = new TextureCache(this, gl);
		this.height = height;
	}
	
	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}


	
	private class CacheListener implements RequestListener<TileKey, byte[]> {

		@Override
		public void requestCompleted(TileKey k, byte[] v) {
			post();
		}
	}
	
	private class TileKey {
		public Tile tile;
		public TileType type;
	}
	private MemoryCache<TileKey, byte[]> memCache;
	
	private synchronized boolean isQuit() {
		return quit;
	}

	private synchronized boolean isRunning() {
		return running;
	}

	private synchronized boolean isPosted() {
		return posted;
	}

	// Benachrichtigt den Renderer, dass mindestens ein Frame gerendert 
	// werden muss. Wenn vorher start() aufgerufen wurde, hat die 
	// Methode u.U. keine Auswirkung. Asynchron, wartet nicht bis der
	// Frame gezeichnet wurde.
	public synchronized void post() {
		posted = true;
	}

	// Beginnt mit einer konstanten FPS-Zahl zu rendern, zB. 60.
	// Asynchron, kehrt sofort zur�ck.
	public synchronized void start() {
		running = true;
	}
	
	// Beendet eine Renderschleife, die mit start() angesto�en wurde.
	// U.U. wird trotzdem noch ein Frame gerendert, falls w�hrenddessen
	// post() aufgerufen wurde.
	public synchronized void stop() {
		running = false;
	}

	@Override
	/*
	 * TODO wenn beides false ist thread anhalten!
	 */
	public void run() {
		while (!isQuit()) {
			if (isRunning() || isPosted()) {
				
				//code
				render();
				
				synchronized (this) {
					posted = false;
				}
			}
		}
	}

	private void render() {
		
	}
	
	private void initialize() {
		
	}
	
	public void quit() {
		quit = true;
	}

	private class RendererEventListener implements GLEventListener {

		@Override
		public void display(GLAutoDrawable draw) {
			post();
		}

		@Override
		public void dispose(GLAutoDrawable draw) {
		}

		@Override
		public void init(GLAutoDrawable draw) {
			initialize();
		}

		@Override
		public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3,
				int arg4) {
			
		}	
	}

	@Override
	public void requestCompleted(Tile k, Integer v) {		
	}
	
}
