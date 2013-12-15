package de.joglearth.rendering;

import static javax.media.opengl.GL2.*;
import static java.lang.Math.*;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.ImmModeSink;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import de.joglearth.geometry.Camera;
import de.joglearth.geometry.CameraListener;
import de.joglearth.geometry.CameraUtils;
import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.Matrix4;
import de.joglearth.geometry.PlaneGeometry;
import de.joglearth.geometry.SphereGeometry;
import de.joglearth.geometry.Tile;
import de.joglearth.settings.Settings;
import de.joglearth.settings.SettingsContract;
import de.joglearth.settings.SettingsListener;
import de.joglearth.source.SourceListener;
import de.joglearth.source.opengl.VertexBuffer;
import de.joglearth.surface.LocationManager;
import de.joglearth.surface.MapLayout;
import de.joglearth.surface.SingleMapType;
import de.joglearth.surface.SurfaceListener;
import de.joglearth.surface.TextureManager;
import de.joglearth.surface.TileMeshManager;
import de.joglearth.surface.TiledMapType;
import de.joglearth.util.AWTInvoker;
import de.joglearth.util.Resource;

import java.awt.Font;


/**
 * Handles the OpenGL rendering.
 * 
 */
public class Renderer {

    private GLCanvas canvas;
    private FPSAnimator animator;
    private int leastHorizontalTiles;
    private int tileSubdivisions = 7;
    private boolean isPosted = false;
    private boolean isRunning = false;
    private int TILE_SIZE = 256;
    private LocationManager locationManager;
    private TextureManager textureManager;
    private Camera camera;
    private DisplayMode activeDisplayMode = DisplayMode.SOLAR_SYSTEM;
    private TiledMapType activeMapType = TiledMapType.OSM2WORLD;
    private MapLayout mapLayout = MapLayout.TILED;
    private SingleMapType singleMapType = SingleMapType.SATELLITE;
    private TiledMapType tiledMapType;
    private Texture kidsWorldMap;
    private Texture satellite;
    private Texture moon;
    private Texture sun;
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
     */
    public Renderer(GLCanvas canv, LocationManager locationManager) {
        this.locationManager = locationManager;
        this.canvas = canv;
        canv.addGLEventListener(new RendererEventListener());

        locationManager.addSurfaceListener(new SurfaceValidator());

        camera = new Camera(new PlaneGeometry());
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
            if (isRunning) {
                return;
            }
            isRunning = true;
        }
        
        AWTInvoker.invoke(new Runnable() {

            @Override
            public void run() {
                boolean doContinue;
                do {
                    synchronized (this) {
                        isPosted = false;
                    }
                    canvas.display();
                    synchronized (this) {
                        doContinue = isPosted && !animator.isAnimating();
                    }
                } while (doContinue);
                synchronized(this) {
                    isRunning = false;
                }
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
        System.out.println("------------- NEW FRAME -------------");
        
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadMatrixd(camera.getProjectionMatrix().doubles(), 0);
        
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadMatrixd(camera.getModelViewMatrix().doubles(), 0);
        
        int zoomLevel = CameraUtils.getOptimalZoomLevel(camera, leastHorizontalTiles);

        if (activeDisplayMode == DisplayMode.SOLAR_SYSTEM) {
            renderSolarSystem();
        } else {
            Iterable<Tile> tile = CameraUtils.getVisibleTiles(camera, zoomLevel);
            renderMeshes(gl, tile);
        } 
        // glClear();
        // if (!sonnensystem) getVisibleTiles() else visibleTile = {zoom = 0, lon = 0, lat
        // = 0}

        // TileMeshSource.request(visibleTile)
        //
        // for (...) textur setzen vbo rendern
    }
    
    public Camera getCamera() {
        return camera;
    }

    private void initialize(GL2 gl) {
        
        gl.glEnable(GL_DEPTH_TEST);
        gl.glEnable(GL_CULL_FACE);     
        gl.glEnable(GL_TEXTURE_2D);
        //gl.glPolygonMode(GL_FRONT_AND_BACK,  GL_LINE);

        this.textureManager = new TextureManager(gl);
        ///textureManager.addSurfaceListener(new SurfaceValidator());

        /* Loads the kidsWorldMap, earth-texture, sun-texture, moon-texture */
        loadTextures();

        /* Loads all POI-textures */
        loadPOITextures();

        /* Get DisplayMode from Settings */
        Settings.getInstance().addSettingsListener(SettingsContract.DISPLAY_MODE,
                new SettingsChanged());
        
        leastHorizontalTiles = canvas.getWidth() / TILE_SIZE;
        tileMeshManager = new TileMeshManager(gl, null);
        tileMeshManager.setTileSubdivisions(tileSubdivisions);
        applyDisplayMode();
                
        animator = new FPSAnimator(60);
    }
    
    
    GLU glu = new GLU();
    GLUquadric quadric;
    private void renderSolarSystem() {
        if (quadric==null) {
            quadric=glu.gluNewQuadric();
            glu.gluQuadricOrientation(quadric, GLU.GLU_OUTSIDE);
        }
        glu.gluSphere(quadric, 1, 100, 50);
    }

    private void renderMeshes(GL2 gl, Iterable<Tile> tiles) {

        for (Tile tile : tiles) {
            Integer texture = textureManager.getTexture(tile);
            
            gl.glBindTexture(GL_TEXTURE_2D, texture);
            
            VertexBuffer vbo = tileMeshManager.requestObject(tile, null).value;

            // Bind vertex buffer
            gl.glBindBuffer(GL_ARRAY_BUFFER, vbo.vertices);
            GLError.throwIfActive(gl);

            // Set vertex / normal / texcoord pointers
            gl.glEnableClientState(GL_VERTEX_ARRAY);
            GLError.throwIfActive(gl);

            gl.glVertexPointer(3, GL_FLOAT, 8*4, 5*4);
            GLError.throwIfActive(gl);

            gl.glEnableClientState(GL_NORMAL_ARRAY);
            GLError.throwIfActive(gl);

            gl.glNormalPointer(GL_FLOAT, 8*4, 2*4);
            GLError.throwIfActive(gl);

            gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);
            GLError.throwIfActive(gl);

            gl.glTexCoordPointer(2, GL_FLOAT, 8*4, 0);
            GLError.throwIfActive(gl);
            
            // Bind index buffer
            gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo.indices);
            GLError.throwIfActive(gl);

            // Draw
            gl.glDrawElements(vbo.primitiveType, vbo.indexCount, GL_UNSIGNED_INT, 0);
            GLError.throwIfActive(gl);

            // Disable pointers
            gl.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
            GLError.throwIfActive(gl);
            
            gl.glDisableClientState(GL_NORMAL_ARRAY);
            GLError.throwIfActive(gl);
            
            gl.glDisableClientState(GL_VERTEX_ARRAY);
            GLError.throwIfActive(gl);

            gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
            GLError.throwIfActive(gl);
        }

    }

    /* Loads the kidsWorldMap, earth-texture, sun-texture, moon-texture */
    private void loadTextures() {

        /* Loads texture: kidsWorldMap 
        kidsWorldMap = TextureIO.newTexture(Resource.loadTextureData(
                "textures/kidsWorldMap.jpg", "jpg"));
        satellite = TextureIO.newTexture(Resource.loadTextureData(
                "textures/earth.jpg", "jpg"));
        moon = TextureIO.newTexture(Resource.loadTextureData(
                "textures/moon.jpg", "jpg"));*/
        // sun = TextureIO.newTexture(Resource.loadTextureData(
        // "textures/sun.jpg", "jpg"));
    }

    /* Loads all POI-textures */
    private void loadPOITextures() {
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
                setDisplayMode(Enum.valueOf(DisplayMode.class, (String) valNew));
            } else if (key.equals(SettingsContract.LEVEL_OF_DETAILS)) {
                setLevelOfDetail(Enum.valueOf(LevelOfDetail.class, (String) valNew));
            } else if (key.equals(SettingsContract.HEIGHT_MAP_ENABLED)) {
                setHeightMapEnabled((Boolean) valNew);
            }

            post();
        }

        private synchronized void setHeightMapEnabled(Boolean valNew) {
            if (tileMeshManager != null) {
                tileMeshManager.setHeightMapEnabled(valNew);
            }
        }

        private synchronized void setLevelOfDetail(LevelOfDetail lod) {
            switch (lod) {
                case LOW:
                    tileSubdivisions = 1;
                    break;
                case MEDIUM:
                    tileSubdivisions = 7;
                    break;
                case HIGH:
                    tileSubdivisions = 15;
            }
            if (tileMeshManager != null) {
                tileMeshManager.setTileSubdivisions(tileSubdivisions);
            }
        }

    }

    /*
     * A Listener to handle events for OpenGL rendering.
     */
    private class RendererEventListener implements GLEventListener {

        @Override
        public void display(GLAutoDrawable drawable) {
            camera.setUpdatesEnabled(false);
            render(drawable.getGL().getGL2());
            camera.setUpdatesEnabled(true);
        }

        @Override
        public void dispose(GLAutoDrawable drawable) {
            camera.setUpdatesEnabled(false);
            stop();
            camera.setUpdatesEnabled(true);
        }

        @Override
        public void init(GLAutoDrawable drawable) {
            camera.setUpdatesEnabled(false);
            initialize(drawable.getGL().getGL2());
            camera.setUpdatesEnabled(true);
        }

        @Override
        public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
            camera.setUpdatesEnabled(false);
            
            leastHorizontalTiles = canvas.getWidth() / TILE_SIZE;
            double aspectRatio = (double) width / (double) height;
            double fov = (double) PI / 2; // TODO
            double near = 0.1; // TODO
            double far = 100.0; // TODO
            camera.setPerspective(fov, aspectRatio, near, far);
            
            camera.setUpdatesEnabled(true);
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

    
    private void applyDisplayMode() {
        switch (activeDisplayMode) {
            case GLOBE_MAP:
                if (tileMeshManager != null) {
                    tileMeshManager.setTessellator(new SphereTessellator());
                }
                // fall through
                
            case SOLAR_SYSTEM:            
                camera.setGeometry(new SphereGeometry());
                break;
                
            case PLANE_MAP:
                if (tileMeshManager != null)  {
                    tileMeshManager.setTessellator(new PlaneTessellator());
                }
                camera.setGeometry(new PlaneGeometry());
        }
    }
    

    /**
     * Sets the {@link de.joglearth.rendering.DisplayMode} to a given value.
     * 
     * @param m The new <code>DisplayMode</code>
     */
    public synchronized void setDisplayMode(DisplayMode m) {
        if (m != activeDisplayMode) {
            activeDisplayMode = m;
            applyDisplayMode();
            post();
        }
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
