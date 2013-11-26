package de.joglearth.rendering;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;

import de.joglearth.geometry.Camera;
import de.joglearth.geometry.CameraListener;
import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.Tile;
import de.joglearth.surface.HeightMap;
import de.joglearth.surface.LocationManager;
import de.joglearth.surface.MapLayout;
import de.joglearth.surface.SingleMapType;
import de.joglearth.surface.TiledMapType;
import de.joglearth.surface.SurfaceListener;
import de.joglearth.surface.TextureManager;
import de.joglearth.settings.SettingsListener;
import de.joglearth.source.SourceListener;
import de.joglearth.source.osm.OSMTileSource;


/**
 * Handles the OpenGL rendering.
 */
public class Renderer {

    private GL2 gl;
    private boolean quit = false;
    private boolean running;
    private boolean posted;
    private LocationManager locationManager;
    private TextureManager textureManager;
    private Camera camera;
    private DisplayMode activeDisplayMode;
    private TiledMapType activeMapType;
    private MapLayout mapLayout;
    private SingleMapType singleMapType;
    private TiledMapType tiledMapType;
    private DisplayMode displayMode;


    private class Worker implements Runnable {

        @Override
        public void run() {
            // TODO Automatisch generierter Methodenstub
            /*
             * while (!isQuit()) { if (isRunning() || isPosted()) {
             * 
             * // code render();
             * 
             * synchronized (this) { posted = false; } } }
             */
        }
    }
    
    
    private class SurfaceValidator implements SurfaceListener {

        @Override
        public void surfaceChanged(double lonFrom, double latFrom, double lonTo, double latTo) {
            GeoCoordinates[] edges = { new GeoCoordinates(lonFrom, latFrom),
                                       new GeoCoordinates(lonFrom,  latTo),
                                       new GeoCoordinates(lonTo, latFrom),
                                       new GeoCoordinates(lonTo, latTo) };
            for (GeoCoordinates geo: edges) {
                if (camera.isPointVisible(geo)) {
                    post(); 
                    break;
                }
            }
        }        
    }
    
    
    private class GraphicsSettingsListener implements SettingsListener {

        @Override
        public void settingsChanged(String key, Object valOld, Object valNew) {
            // TODO Automatisch generierter Methodenstub
            
        }        
    }


    /**
     * Constructor initializes the OpenGL functionalities.
     * 
     * @param canv {@link com.jogamp.opengl.swt.GLCanvas} object of the GUI.
     * @param height {@link HeightMap} that provides the height of a point.
     * @param locationManager {@link LocationManager} that provides the information about Overlays
     *        to be displayed.
     * @param camera TODO {@link Camera}
     */
    public Renderer(GLCanvas canv, LocationManager locationManager, Camera camera) {
        this.locationManager = locationManager;
        this.camera = camera;
        this.gl = canv.getGL().getGL2();
        canv.addGLEventListener(new RendererEventListener());
        this.textureManager = new TextureManager(gl);
        
        SurfaceValidator surfaceValidator = new SurfaceValidator();
        textureManager.addSurfaceListener(surfaceValidator);
        locationManager.addSurfaceListener(surfaceValidator);
        
        
        
        camera.addCameraListener(new CameraListener() {
            
            @Override
            public void cameraViewChanged() {
                post();
            }
        });
    }

    /*
     * TODO Returns if the window is quit.
     * 
     * @return Is the window quit?
     * 
     * private synchronized boolean isQuit() { return quit; }
     * 
     * /** Returns if the OpenGL rendering loop is running.
     * 
     * @return Is OpenGL rendering loop running?
     * 
     * private synchronized boolean isRunning() { return running; }
     * 
     * /** Returns if the surface has changed and a new render process is needed.
     * 
     * @return Should the window be re-rendered.
     * 
     * private synchronized boolean isPosted() { return posted; }
     */

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

    /*
     * Re-renders the OpenGL view.
     * 
     * private void render() {
     * 
     * }
     * 
     * /** TODO
     * 
     * Initializes the OpenGL settings.
     * 
     * private void initialize() {
     * 
     * }
     */

    /**
     * Quits the Renderer thread.
     */
    public void quit() {
        quit = true;
    }


    /**
     * A Listener to handle events for OpenGL rendering.
     */
    private class RendererEventListener implements GLEventListener {

        @Override
        public void display(GLAutoDrawable drawable) {
            post();
        }

        @Override
        public void dispose(GLAutoDrawable drawable) {}

        @Override
        public void init(GLAutoDrawable drawable) {
            // initialize();
        }

        @Override
        public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

        }
    }

    public void setDisplayMode(DisplayMode m) {
        displayMode = m;
        post();
    }

    public void setMapType(SingleMapType t) {
        mapLayout = MapLayout.SINGLE;
        singleMapType = t;
        post();
    }

    public void setMapType(TiledMapType t) {
        mapLayout = MapLayout.TILED;
        tiledMapType = t;
        post();
    }

}
