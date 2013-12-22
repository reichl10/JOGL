package de.joglearth.rendering;

import static javax.media.opengl.GL2.*;
import static java.lang.Math.*;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

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
import de.joglearth.opengl.GLContext;
import de.joglearth.opengl.GLContextListener;
import de.joglearth.opengl.VertexBuffer;
import de.joglearth.settings.Settings;
import de.joglearth.settings.SettingsContract;
import de.joglearth.settings.SettingsListener;
import de.joglearth.source.osm.OSMTileManager;
import de.joglearth.surface.LocationManager;
import de.joglearth.surface.MapLayout;
import de.joglearth.surface.SingleMapType;
import de.joglearth.surface.SurfaceListener;
import de.joglearth.surface.TextureManager;
import de.joglearth.surface.TileMeshManager;
import de.joglearth.surface.TiledMapType;
import de.joglearth.util.Resource;


/**
 * Handles the OpenGL rendering.
 * 
 */
public class Renderer {

    private GLContext gl;
    private int leastHorizontalTiles;
    private int tileSubdivisions = 7;
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
    public Renderer(GLContext gl, LocationManager locationManager) {
        this.locationManager = locationManager;
        this.gl = gl;
        gl.addGLContextListener(new RendererEventListener());

        locationManager.addSurfaceListener(new SurfaceValidator());

        camera = new Camera(new PlaneGeometry());
        camera.addCameraListener(new CameraListener() {

            @Override
            public void cameraViewChanged() {
                Renderer.this.gl.postRedisplay();
            }
        });
    }
    
    // TODO Re-renders the OpenGL view.
    private void render() {
        System.err.println("------------- NEW FRAME -------------");
        
        gl.clear();
        gl.loadMatrix(GL_PROJECTION, camera.getProjectionMatrix());
        gl.loadMatrix(GL_MODELVIEW, camera.getModelViewMatrix());
           
        //TODO !!!!
        /*
         * ----------------------------
         * limited zoom to 3 !!!
         * ----------------------------
         * 
         */
        int zoomLevel = Math.min(18, CameraUtils.getOptimalZoomLevel(camera, leastHorizontalTiles));
        System.err.print("zoomlevel: " + zoomLevel + "  ");
        
        
        if (activeDisplayMode == DisplayMode.SOLAR_SYSTEM) {
            renderSolarSystem();
        } else {
            Iterable<Tile> tile = CameraUtils.getVisibleTiles(camera, zoomLevel);
            renderMeshes(tile);
        } 
    }
    
    public Camera getCamera() {
        return camera;
    }

    private void initialize() {        
        gl.setFeatureEnabled(GL_DEPTH_TEST, true);
        gl.setFeatureEnabled(GL_CULL_FACE, true);
        gl.setFeatureEnabled(GL_TEXTURE_2D, true);

        textureManager = new TextureManager(gl, OSMTileManager.getInstance(), 200);
        ///textureManager.addSurfaceListener(new SurfaceValidator());

        /* Loads the kidsWorldMap, earth-texture, sun-texture, moon-texture */
        loadTextures();

        /* Loads all POI-textures */
        loadPOITextures();

        /* Get DisplayMode from Settings */
        Settings.getInstance().addSettingsListener(SettingsContract.DISPLAY_MODE,
                new SettingsChanged());
        
        leastHorizontalTiles = gl.getSize().width / TILE_SIZE;
        tileMeshManager = new TileMeshManager(gl, null);
        tileMeshManager.setTileSubdivisions(tileSubdivisions);
        applyDisplayMode();
    }
    
    
    private void dispose() {
        textureManager.dispose();
        textureManager = null;
        
        tileMeshManager.dispose();
        tileMeshManager = null;
    }
    

    private void renderSolarSystem() {
        gl.drawSphere(1, 100, 50, false, satellite);
    }

    
    
    private void renderMeshes(Iterable<Tile> tiles) {

        for (Tile tile : tiles) {
            Texture texture = textureManager.getTexture(tile);
            VertexBuffer vbo = tileMeshManager.requestObject(tile, null).value;
            gl.drawVertexBuffer(vbo, texture);
        }

    }

    /* Loads the kidsWorldMap, earth-texture, sun-texture, moon-texture */
    private void loadTextures() {
        try {
            kidsWorldMap = gl.loadTexture(Resource.open("textures/kidsWorldMap.jpg"), "jpg", true);
            satellite = gl.loadTexture(Resource.open("textures/earth.jpg"), "jpg", true);
            moon = gl.loadTexture(Resource.open("textures/moon.jpg"), "jpg", true);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load texture from resource", e);
        }
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
    private class RendererEventListener implements GLContextListener {

        @Override
        public void beginFrame(GLContext context) {
            camera.setUpdatesEnabled(false);
        }
        
        @Override
        public void endFrame(GLContext context) {
            camera.setUpdatesEnabled(true);
        }
        
        @Override
        public void display(GLContext context) {
            render();
        }

        @Override
        public void dispose(GLContext context) {
            Renderer.this.dispose();
        }

        @Override
        public void initialize(GLContext context) {
            Renderer.this.initialize();
        }

        @Override
        public void reshape(GLContext context, int width, int height) {
            leastHorizontalTiles = context.getSize().width / TILE_SIZE;
            double aspectRatio = (double) width / (double) height;
            double fov = (double) PI / 2; // TODO
            double near = 0.01; // TODO
            double far = 100.0; // TODO
            camera.setPerspective(fov, aspectRatio, near, far);
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
                    gl.postRedisplay();
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
        gl.postRedisplay();
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
        gl.postRedisplay();
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
        gl.postRedisplay();
    }
}
