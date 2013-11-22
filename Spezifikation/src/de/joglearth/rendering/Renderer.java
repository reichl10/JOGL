package de.joglearth.rendering;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;

import de.joglearth.geometry.Camera;
import de.joglearth.geometry.CameraListener;
import de.joglearth.geometry.Tile;
import de.joglearth.surface.HeightMapManager;
import de.joglearth.surface.LocationManager;
import de.joglearth.surface.SurfaceListener;
import de.joglearth.surface.TextureManager;
import de.joglearth.settings.SettingsListener;
import de.joglearth.source.SourceListener;
import de.joglearth.source.osm.OSMTileSource;


/**
 * Handles the OpenGL rendering.
 */
public class Renderer implements Runnable, CameraListener, SettingsListener, SurfaceListener {

    private GL2 gl;
    private boolean quit = false;
    private boolean running;
    private boolean posted;
    private LocationManager locationManager;
    private TextureManager textureManager;
    private Camera camera;


    /**
     * Constructor initializes the OpenGL functionalities.
     * 
     * @param canv {@link com.jogamp.opengl.swt.GLCanvas} object of the GUI.
     * @param height {@link HeightMapManager} that provides the height of a point.
     * @param locationManager {@link LocationManager} that provides the information about Overlays
     *        to be displayed.
     * @param camera //TODO {@link Camera}
     */
    public Renderer(GLCanvas canv, LocationManager locationManager, Camera camera) {
        this.locationManager = locationManager;
        this.camera = camera;
        this.gl = canv.getGL().getGL2();
        canv.addGLEventListener(new RendererEventListener());
        this.textureManager = new TextureManager(gl);
        textureManager.addSurfaceListener(this);
    }

    /**
     * Gets the Camera.
     * 
     * @return Camera object.
     */
    public Camera getCamera() {
        return camera;
    }

    /**
     * @param camera
     */
    public void setCamera(Camera camera) {
        this.camera = camera;
    }


    /**
     * 
     * Listener notified when a new element arrived in a {@link Cache}.
     * 
     */
    private class CacheListener implements SourceListener<Tile, byte[]> {

        @Override
        public void requestCompleted(Tile k, byte[] v) {
            post();
        }
    }


    /**
     * @return
     */
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
    /**
     * Notifies the <code>Renderer</code> that a new frame should be rendered. If
     * <code>start()</code> is called this method may have no effect. Asynchron method, does not
     * wait till a frame is drawn.
     */
    public synchronized void post() {
        posted = true;
    }

    // Beginnt mit einer konstanten FPS-Zahl zu rendern, zB. 60.
    // Asynchron, kehrt sofort zur�ck.
    /**
     * Starts the render loops with 60 FPS.
     */
    public synchronized void start() {
        running = true;
    }

    // Beendet eine Renderschleife, die mit start() angesto�en wurde.
    // U.U. wird trotzdem noch ein Frame gerendert, falls w�hrenddessen
    // post() aufgerufen wurde.
    /**
     * Stops the rendering loop. When <code>post()</code> is called a new frame will be rendered.
     */
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

                // code
                render();

                synchronized (this) {
                    posted = false;
                }
            }
        }
    }

    // TODO
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
        public void dispose(GLAutoDrawable draw) {}

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
    public void settingsChanged(String key, Object valOld, Object valNew) {
        // TODO Automatisch erstellter Methoden-Stub

    }

    @Override
    public void cameraViewChanged() {
        // TODO Automatisch erstellter Methoden-Stub

    }

    @Override
    public void surfaceChanged(Tile tile) {
        // TODO Automatisch generierter Methodenstub
        
    }

}
