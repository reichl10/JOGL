package de.joglearth.rendering;

import static javax.media.opengl.GL.GL_ARRAY_BUFFER;
import static javax.media.opengl.GL.GL_FLOAT;
import static javax.media.opengl.GL.GL_NO_ERROR;
import static javax.media.opengl.GL.GL_UNSIGNED_INT;
import static javax.media.opengl.fixedfunc.GLPointerFunc.GL_VERTEX_ARRAY;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import jogamp.opengl.GLVersionNumber;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

import de.joglearth.geometry.Camera;
import de.joglearth.geometry.CameraListener;
import de.joglearth.geometry.CameraUtils;
import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.PlaneGeometry;
import de.joglearth.geometry.SphereGeometry;
import de.joglearth.geometry.Tile;
import de.joglearth.settings.Settings;
import de.joglearth.settings.SettingsContract;
import de.joglearth.settings.SettingsListener;
import de.joglearth.source.SourceListener;
import de.joglearth.source.opengl.VertexBuffer;
import de.joglearth.surface.HeightMap;
import de.joglearth.surface.LocationManager;
import de.joglearth.surface.MapLayout;
import de.joglearth.surface.SingleMapType;
import de.joglearth.surface.SurfaceListener;
import de.joglearth.surface.TextureManager;
import de.joglearth.surface.TileMeshManager;
import de.joglearth.surface.TiledMapType;
import de.joglearth.util.AWTInvoker;
import de.joglearth.util.Resource;


/**
 * Handles the OpenGL rendering.
 * 
 */
public class Renderer {

    private GLCanvas canvas;
    private FPSAnimator animator;
    private int leastHorizontalTiles;
    private int levelOfDetail;
    private boolean heightMapEnabled;
    private boolean isPosted = false;
    private int TILE_SIZE = 256;
    private LocationManager locationManager;
    private TextureManager textureManager;
    private Camera camera;
    private DisplayMode activeDisplayMode;
    private TiledMapType activeMapType;
    private MapLayout mapLayout;
    private SingleMapType singleMapType;
    private TiledMapType tiledMapType;
    private Texture kidsWorldMap;
    private Texture satellite;
    private Texture moon;
    private Texture sun;
    private Tessellator tessellator;
    private TileMeshManager tileMeshManager;

    private Map<String, Texture> poiTextures;
    private final String[] POI_NAMES = new String[] { "Activity", "Bank", "Education", "Grocery",
            "Health", "HikingCycling", "Hotel", "Nightlife", "Post", "Restaurant", "Shop",
            "Toilets" };


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
        this.canvas = canv;
        canv.addGLEventListener(new RendererEventListener());

        locationManager.addSurfaceListener(new SurfaceValidator());

        camera.addCameraListener(new CameraListener() {

            @Override
            public void cameraViewChanged() {
                post();
            }
        });
    }

    /**
     * Notifies the {@link de.joglearth.rendering.Renderer} that a new frame should be rendered. If
     * <code>start()</code> is called this method may have no effect. Asynchronous method, does not
     * wait until a frame is drawn.
     */
    public void post() {
        synchronized (this) {
            if (isPosted || animator.isAnimating()) {
                return;
            }
            isPosted = true;
        }
        AWTInvoker.invoke(new Runnable() {

            @Override
            public void run() {
                boolean isPostedCopy;
                do {
                    canvas.display();
                    synchronized (this) {
                        isPostedCopy = isPosted;
                    }
                } while (isPostedCopy && !animator.isAnimating());
            }
        });
    }

    // Asynchron, kehrt sofort zurück.
    /**
     * Starts the render loop with 60 FPS.
     */
    public synchronized void start() {
        animator.start();
    }

    /**
     * Stops the render loop. When <code>post()</code> is called a new frame will be rendered.
     */
    public synchronized void stop() {
        animator.stop();
    }

    // TODO Re-renders the OpenGL view.
    private void render(GL2 gl) {

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL2.GL_PROJECTION);

        int zoomLevel = CameraUtils.getOptimalZoomLevel(camera, leastHorizontalTiles);

        if (activeDisplayMode == DisplayMode.SOLAR_SYSTEM) {
            startSolarSystem();
        } else if (activeDisplayMode == DisplayMode.GLOBE_MAP) {

            Iterable<Tile> tile = CameraUtils.getVisibleTiles(camera, zoomLevel);

            renderMeshes(gl, tile);

        } else if (activeDisplayMode == DisplayMode.PLANE_MAP) {

            Iterable<Tile> tile = CameraUtils.getVisibleTiles(camera, zoomLevel);
            renderMeshes(gl, tile);

        } else {
            // TODO
        }

        // glClear();
        // if (!sonnensystem) getVisibleTiles() else visibleTile = {zoom = 0, lon = 0, lat
        // = 0}

        // TileMeshSource.request(visibleTile)
        //
        // for (...) textur setzen vbo rendern

    }

    private void initialize(GL2 gl) {

        this.textureManager = new TextureManager(gl);
        textureManager.addSurfaceListener(new SurfaceValidator());

        /* Loads the kidsWorldMap, earth-texture, sun-texture, moon-texture */
        loadTextures();

        /* Loads all POI-textures */
        loadPoi();

        /* Get DisplayMode from Settings */
        Settings.getInstance().addSettingsListener(SettingsContract.DISPLAY_MODE,
                new SettingsChanged());

        leastHorizontalTiles = canvas.getWidth() / TILE_SIZE;
        tileMeshManager = new TileMeshManager(gl, tessellator);

        animator = new FPSAnimator(60);
    }

    private void startSolarSystem() {
        // TODO Fabian's Sonnensystem einbinden
    }

    private void renderMeshes(GL2 gl, Iterable<Tile> tile) {

        ArrayList<VertexBuffer> meshes = new ArrayList<VertexBuffer>();
        tileMeshManager.setTessellator(tessellator);

        for (Tile t : tile) {
            meshes.add(tileMeshManager.requestObject(t, new SourceChanged()).value);
        }

        for (VertexBuffer vbo : meshes) {

            gl.glBindBuffer(GL_ARRAY_BUFFER, vbo.vertices);
            GLError.throwIfActive(gl);

            gl.glEnableClientState(GL_VERTEX_ARRAY);
            GLError.throwIfActive(gl);

            gl.glVertexPointer(3, GL_FLOAT, 0, 0);
            GLError.throwIfActive(gl);

            gl.glDrawElements(vbo.primitiveType, vbo.primitiveCount, GL_UNSIGNED_INT, vbo.indices);
            GLError.throwIfActive(gl);

            gl.glDisableClientState(GL_VERTEX_ARRAY);
            GLError.throwIfActive(gl);

            gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
            GLError.throwIfActive(gl);
        }

    }

    /* Loads the kidsWorldMap, earth-texture, sun-texture, moon-texture */
    private void loadTextures() {

        /* Loads texture: kidsWorldMap */
        kidsWorldMap = TextureIO.newTexture(Resource.loadTextureData(
                "textures/kidsWorldMap.jpg", "jpg"));
        satellite = TextureIO.newTexture(Resource.loadTextureData(
                "textures/earth.jpg", "jpg"));
        moon = TextureIO.newTexture(Resource.loadTextureData(
                "textures/moon.jpg", "jpg"));
        // sun = TextureIO.newTexture(Resource.loadTextureData(
        // "textures/sun.jpg", "jpg"));
    }

    /* Loads all POI-textures */
    private void loadPoi() {
        poiTextures = new LinkedHashMap<>();
        for (String name : POI_NAMES) {
            poiTextures.put(name, TextureIO.newTexture(Resource.loadTextureData("iconsPoi/POI_" 
                    + name + ".png", "png")));
        }
    }


    /* SettingsListener */
    private class SettingsChanged implements SettingsListener {

        @Override
        public void settingsChanged(String key, Object valOld, Object valNew) {

            if (key.equals(SettingsContract.DISPLAY_MODE)) {
                setDisplayMode((DisplayMode) valNew);
            } else if (key.equals(SettingsContract.LEVEL_OF_DETAILS)) {
                setLevelOfDetail((LevelOfDetail) valNew);
            } else if (key.equals(SettingsContract.HEIGHT_MAP_ENABLED)) {
                setHeightMapEnabled((Boolean) valNew);
            }

            post();
        }

        private synchronized void setHeightMapEnabled(Boolean valNew) {
            heightMapEnabled = valNew;

        }

        private synchronized void setLevelOfDetail(LevelOfDetail valNew) {
            switch (valNew) {
                case LOW:
                    levelOfDetail = 0;
                    break;
                case MEDIUM:
                    levelOfDetail = 5;
                    break;
                case HIGH:
                    levelOfDetail = 10;
            }

        }

    }

    /*
     * A Listener to handle events for OpenGL rendering.
     */
    private class RendererEventListener implements GLEventListener {

        @Override
        public void display(GLAutoDrawable drawable) {
            render(drawable.getGL().getGL2());
        }

        @Override
        public void dispose(GLAutoDrawable drawable) {
            stop();
        }

        @Override
        public void init(GLAutoDrawable drawable) {
            initialize(drawable.getGL().getGL2());
        }

        @Override
        public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
            leastHorizontalTiles = canvas.getWidth() / TILE_SIZE;
            double aspectRatio = (double) width / (double) height;
            double fov = (double) Math.PI / 2; // TODO
            double near = 0.1; // TODO
            double far = 1000.0; // TODO
            camera.setPerspective(fov, aspectRatio, near, far);
        }
    }

    private class SourceChanged implements SourceListener<Tile, VertexBuffer> {

        @Override
        public void requestCompleted(Tile key, VertexBuffer value) {
            // TODO

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


    /**
     * Sets the {@link de.joglearth.rendering.DisplayMode} to a given value.
     * 
     * @param m The new <code>DisplayMode</code>
     */
    private synchronized void setDisplayMode(DisplayMode m) {
        // TODO Braucht man das hier?
        activeDisplayMode = m;

        if (activeDisplayMode == DisplayMode.GLOBE_MAP) {
            camera.setGeometry(new SphereGeometry());
            tessellator = new SphereTessellator();
        } else if (activeDisplayMode == DisplayMode.PLANE_MAP) {
            camera.setGeometry(new PlaneGeometry());
            tessellator = new PlaneTessellator();
        } else {
            camera.setGeometry(new SphereGeometry());
            tessellator = new SphereTessellator();
        }

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
