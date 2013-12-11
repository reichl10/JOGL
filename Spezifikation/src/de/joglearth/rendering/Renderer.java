package de.joglearth.rendering;

import java.io.IOException;
import java.io.InputStream;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

import de.joglearth.geometry.Camera;
import de.joglearth.geometry.CameraListener;
import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.settings.SettingsListener;
import de.joglearth.surface.LocationManager;
import de.joglearth.surface.MapLayout;
import de.joglearth.surface.SingleMapType;
import de.joglearth.surface.SurfaceListener;
import de.joglearth.surface.TextureManager;
import de.joglearth.surface.TiledMapType;


/**
 * Handles the OpenGL rendering.
 * 
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
    private Texture kidsWorldMap;
    private Texture satellite;
    private Texture moon;
    private Texture sun;
    private Texture poiActivity, poiBank, poiEducation, poiGrocery, poiHealth,
            poiHikingCycling, poiHotel, poiNightlife, poiPost, poiRestaurant, poiShop, poiToilets;


    private class Worker implements Runnable {

        @Override
        public void run() {

            while (!isQuit()) {
                if (isRunning() || isPosted()) {
                    render();

                    synchronized (this) {
                        posted = false;
                    }
                } else {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        // TODO
                    }
                }
            }
        }
    }

    private class SurfaceValidator implements SurfaceListener {

        @Override
        public void surfaceChanged(double lonFrom, double latFrom, double lonTo, double latTo) {
            GeoCoordinates[] edges = { new GeoCoordinates(lonFrom, latFrom),
                    new GeoCoordinates(lonFrom, latTo),
                    new GeoCoordinates(lonTo, latFrom),
                    new GeoCoordinates(lonTo, latTo) };
            for (GeoCoordinates geo : edges) {
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
            post();
        }
    }


    /**
     * Constructor initializes the OpenGL functionalities.
     * 
     * @param canv GLCanvas object of the GUI
     * @param locationManager <code>LocationManager</code> that provides the information about
     *        Overlays to be displayed
     * @param camera <code>Camera</code> object
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
     * Returns if the window is quit.
     * 
     * @return Is the window quit?
     */
    private synchronized boolean isQuit() {
        return quit;
    }

    /*
     * Returns if the OpenGL rendering loop is running.
     * 
     * @return Is OpenGL rendering loop running?
     */
    private synchronized boolean isRunning() {
        return running;
    }

    /*
     * Returns if the surface has changed and a new render process is needed.
     * 
     * @return Should the window be re-rendered.
     */
    private synchronized boolean isPosted() {
        return posted;
    }

    // Benachrichtigt den Renderer, dass mindestens ein Frame gerendert
    // werden muss. Wenn vorher start() aufgerufen wurde, hat die
    // Methode u.U. keine Auswirkung. Asynchron, wartet nicht bis der
    // Frame gezeichnet wurde.
    /**
     * Notifies the {@link de.joglearth.rendering.Renderer} that a new frame should be rendered. If
     * <code>start()</code> is called this method may have no effect. Asynchronous method, does not
     * wait until a frame is drawn.
     */
    public synchronized void post() {
        posted = true;
    }

    // Beginnt mit einer konstanten FPS-Zahl zu rendern, zB. 60.
    // Asynchron, kehrt sofort zurück.
    /**
     * Starts the render loop with 60 FPS.
     */
    public synchronized void start() {
        running = true;
    }

    // Beendet eine Renderschleife, die mit start() angestoßen wurde.
    // U.U. wird trotzdem noch ein Frame gerendert, falls währenddessen
    // post() aufgerufen wurde.
    /**
     * Stops the render loop. When <code>post()</code> is called a new frame will be rendered.
     */
    public synchronized void stop() {
        running = false;
    }

    // TODO Re-renders the OpenGL view.
    private void render() {

        // glClear();
        // if (!sonnensystem) getVisibleTiles() else visibleTile = {zoom = 0, lon = 0, lat
        // = 0} TileMeshSource.request(visibleTile)
        //
        // for (...) textur setzen vbo rendern

    }

    // TODO Initializes the OpenGL settings.
    private void initialize() {

        /* Loads the kidsWorldMap, earth-texture, sun-texture, moon-texture */
        loadMaps();

        /* Loads all POI-textures */
        loadPoi();

        // Zuweisen des activeDisplayMode aus den Settings?
    }

    private void loadMaps() {
        /* Loads texture: kidsWorldMap */
        try {
            InputStream stream = getClass().getResourceAsStream("textures/kidsWorldMap.jpg");
            TextureData data = TextureIO.newTextureData(GLProfile.getDefault(),
                    stream, false, "jpg");
            kidsWorldMap = TextureIO.newTexture(data);
        } catch (IOException ioExc) {
            ioExc.printStackTrace();
            System.exit(1);
        }

        /* Loads texture: earth */
        try {
            InputStream stream = getClass().getResourceAsStream("textures/earth.jpg");
            TextureData data = TextureIO.newTextureData(GLProfile.getDefault(),
                    stream, false, "jpg");
            satellite = TextureIO.newTexture(data);
        } catch (IOException ioExc) {
            ioExc.printStackTrace();
            System.exit(1);
        }

        /* Loads texture: moon */
        try {
            InputStream stream = getClass().getResourceAsStream("textures/moon.jpg");
            TextureData data = TextureIO.newTextureData(GLProfile.getDefault(),
                    stream, false, "jpg");
            moon = TextureIO.newTexture(data);
        } catch (IOException ioExc) {
            ioExc.printStackTrace();
            System.exit(1);
        }

        /* Loads texture: sun */
        try {
            InputStream stream = getClass().getResourceAsStream("textures/sun.jpg");
            TextureData data = TextureIO.newTextureData(GLProfile.getDefault(),
                    stream, false, "jpg");
            sun = TextureIO.newTexture(data);
        } catch (IOException ioExc) {
            ioExc.printStackTrace();
            System.exit(1);
        }
    }

    private void loadPoi() {
        /* Loads POI-texture: Activity */
        try {
            InputStream stream = getClass().getResourceAsStream("iconsPoi/POI_Activity.png");
            TextureData data = TextureIO.newTextureData(GLProfile.getDefault(),
                    stream, false, "png");
            poiActivity = TextureIO.newTexture(data);
        } catch (IOException ioExc) {
            ioExc.printStackTrace();
            System.exit(1);
        }

        /* Loads POI-texture: Bank */
        try {
            InputStream stream = getClass().getResourceAsStream("iconsPoi/POI_Bank.png");
            TextureData data = TextureIO.newTextureData(GLProfile.getDefault(),
                    stream, false, "png");
            poiBank = TextureIO.newTexture(data);
        } catch (IOException ioExc) {
            ioExc.printStackTrace();
            System.exit(1);
        }

        /* Loads POI-texture: Education */
        try {
            InputStream stream = getClass().getResourceAsStream("iconsPoi/POI_Education.png");
            TextureData data = TextureIO.newTextureData(GLProfile.getDefault(),
                    stream, false, "png");
            poiEducation = TextureIO.newTexture(data);
        } catch (IOException ioExc) {
            ioExc.printStackTrace();
            System.exit(1);
        }

        /* Loads POI-texture: Grocery */
        try {
            InputStream stream = getClass().getResourceAsStream("iconsPoi/POI_Grocery.png");
            TextureData data = TextureIO.newTextureData(GLProfile.getDefault(),
                    stream, false, "png");
            poiGrocery = TextureIO.newTexture(data);
        } catch (IOException ioExc) {
            ioExc.printStackTrace();
            System.exit(1);
        }

        /* Loads POI-texture: Health */
        try {
            InputStream stream = getClass().getResourceAsStream("iconsPoi/POI_Health.png");
            TextureData data = TextureIO.newTextureData(GLProfile.getDefault(),
                    stream, false, "png");
            poiHealth = TextureIO.newTexture(data);
        } catch (IOException ioExc) {
            ioExc.printStackTrace();
            System.exit(1);
        }

        /* Loads POI-texture: HikingCycling */
        try {
            InputStream stream = getClass().getResourceAsStream("iconsPoi/POI_HikingCycling.png");
            TextureData data = TextureIO.newTextureData(GLProfile.getDefault(),
                    stream, false, "png");
            poiHikingCycling = TextureIO.newTexture(data);
        } catch (IOException ioExc) {
            ioExc.printStackTrace();
            System.exit(1);
        }

        /* Loads POI-texture: Hotel */
        try {
            InputStream stream = getClass().getResourceAsStream("iconsPoi/POI_Hotel.png");
            TextureData data = TextureIO.newTextureData(GLProfile.getDefault(),
                    stream, false, "png");
            poiHotel = TextureIO.newTexture(data);
        } catch (IOException ioExc) {
            ioExc.printStackTrace();
            System.exit(1);
        }

        /* Loads POI-texture: Nightlife */
        try {
            InputStream stream = getClass().getResourceAsStream("iconsPoi/POI_Nightlife.png");
            TextureData data = TextureIO.newTextureData(GLProfile.getDefault(),
                    stream, false, "png");
            poiNightlife = TextureIO.newTexture(data);
        } catch (IOException ioExc) {
            ioExc.printStackTrace();
            System.exit(1);
        }

        /* Loads POI-texture: Post */
        try {
            InputStream stream = getClass().getResourceAsStream("iconsPoi/POI_Post.png");
            TextureData data = TextureIO.newTextureData(GLProfile.getDefault(),
                    stream, false, "png");
            poiPost = TextureIO.newTexture(data);
        } catch (IOException ioExc) {
            ioExc.printStackTrace();
            System.exit(1);
        }

        /* Loads POI-texture: Restaurant */
        try {
            InputStream stream = getClass().getResourceAsStream("iconsPoi/POI_Restaurant.png");
            TextureData data = TextureIO.newTextureData(GLProfile.getDefault(),
                    stream, false, "png");
            poiRestaurant = TextureIO.newTexture(data);
        } catch (IOException ioExc) {
            ioExc.printStackTrace();
            System.exit(1);
        }

        /* Loads POI-texture: Shop */
        try {
            InputStream stream = getClass().getResourceAsStream("iconsPoi/POI_Shop.png");
            TextureData data = TextureIO.newTextureData(GLProfile.getDefault(),
                    stream, false, "png");
            poiShop = TextureIO.newTexture(data);
        } catch (IOException ioExc) {
            ioExc.printStackTrace();
            System.exit(1);
        }

        /* Loads POI-texture: Toilets */
        try {
            InputStream stream = getClass().getResourceAsStream("iconsPoi/POI_Toilets.png");
            TextureData data = TextureIO.newTextureData(GLProfile.getDefault(),
                    stream, false, "png");
            poiToilets = TextureIO.newTexture(data);
        } catch (IOException ioExc) {
            ioExc.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Quits the {@link de.joglearth.rendering.Renderer} thread.
     */
    public synchronized void quit() {
        quit = true;
    }


    /*
     * A Listener to handle events for OpenGL rendering.
     */
    private class RendererEventListener implements GLEventListener {

        @Override
        public void display(GLAutoDrawable drawable) {
            post();
        }

        @Override
        public void dispose(GLAutoDrawable drawable) {

        }

        @Override
        public void init(GLAutoDrawable drawable) {
            initialize();
        }

        @Override
        public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
            // camera.setPerspective();
        }
    }


    /**
     * Sets the {@link de.joglearth.rendering.DisplayMode} to a given value.
     * 
     * @param m The new <code>DisplayMode</code>
     */
    public void setDisplayMode(DisplayMode m) {
        displayMode = m;
        // camera.setGeometry(newGeo);
        post();
    }

    /**
     * Sets the {@link de.joglearth.surface.MapLayout} to a given value. This type is a
     * {@link de.joglearth.surface.SingleMapType} as it is only one tile as a texture.
     * 
     * @param t The new <code>MapLayout</code>
     */
    public void setMapType(SingleMapType t) {
        mapLayout = MapLayout.SINGLE;
        singleMapType = t;
        post();
    }

    /**
     * Sets the {@link de.joglearth.surface.MapLayout} to a given value. This type is a
     * {@link de.joglearth.surface.TiledMapType} as the texture consists of multiple tiles.
     * 
     * @param t The new <code>MapLayout</code>
     */
    public void setMapType(TiledMapType t) {
        mapLayout = MapLayout.TILED;
        tiledMapType = t;
        post();
    }
}
