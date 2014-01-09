package de.joglearth.rendering;

import static javax.media.opengl.GL2.*;
import static java.lang.Math.*;

import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.Texture;

import de.joglearth.geometry.Camera;
import de.joglearth.geometry.CameraListener;
import de.joglearth.geometry.CameraUtils;
import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.Matrix4;
import de.joglearth.geometry.PlaneGeometry;
import de.joglearth.geometry.ScreenCoordinates;
import de.joglearth.geometry.SphereGeometry;
import de.joglearth.geometry.SurfaceListener;
import de.joglearth.geometry.Tile;
import de.joglearth.geometry.Vector3;
import de.joglearth.height.HeightMap;
import de.joglearth.location.Location;
import de.joglearth.location.LocationManager;
import de.joglearth.location.LocationType;
import de.joglearth.map.MapConfiguration;
import de.joglearth.map.single.SingleMapConfiguration;
import de.joglearth.map.single.SingleMapType;
import de.joglearth.opengl.GLContext;
import de.joglearth.opengl.GLContextListener;
import de.joglearth.opengl.TextureFilter;
import de.joglearth.opengl.VertexBuffer;
import de.joglearth.settings.Settings;
import de.joglearth.settings.SettingsContract;
import de.joglearth.settings.SettingsListener;
import de.joglearth.ui.Messages;
import de.joglearth.util.Resource;


/**
 * Handles the OpenGL rendering.
 * 
 */
public class Renderer {

    private GLContext gl;
    private int tileSubdivisions = 7;
    private LocationManager locationManager;
    private TextureManager textureManager;
    private Camera camera;
    private DisplayMode activeDisplayMode = DisplayMode.SOLAR_SYSTEM;
    private Texture earth, moon;
    private VertexBufferManager tileMeshManager;
    private SurfaceListener surfaceListener = new SurfaceValidator();
    private GLContextListener glContextListener = new RendererGLListener();
    private Map<LocationType, Texture> overlayIconTextures;
    private SettingsListener settingsListener = new GraphicsSettingsListener();
    private Texture sky;
    private MapConfiguration mapConfiguration = new SingleMapConfiguration(SingleMapType.SATELLITE);
    private Dimension screenSize = new Dimension(640, 480);

    private final static int ICON_SIZE = 16;
    
    private enum InitState {
        AWAITING,
        LOADING,
        DONE        
    }
    
    private InitState initState;

    /**
     * Constructor initializes the OpenGL functionalities.
     * 
     * @param canv GLCanvas object of the GUI
     * @param locationManager <code>LocationManager</code> that provides the information about
     *        Overlays to be displayed
     */
    public Renderer(GLContext gl, LocationManager locationManager) {
        if (gl == null || locationManager == null) {
            throw new IllegalArgumentException();
        }
        
        setGLContext(gl);
        setLocationManager(locationManager);

        camera = new Camera(new PlaneGeometry());
        camera.addCameraListener(new CameraListener() {

            @Override
            public void cameraViewChanged() {
                Renderer.this.gl.postRedisplay();
            }
        });
    }
    
    public void setGLContext(GLContext gl) {
        if (gl == null) {
            throw new IllegalArgumentException();
        }
        
        this.gl = gl;
        gl.addGLContextListener(glContextListener);
    }
    
    public void setLocationManager(LocationManager manager) {
        if (manager == null) {
            throw new IllegalArgumentException();
        }
        
        this.locationManager = manager;
        locationManager.addSurfaceListener(surfaceListener);        
    }
    
    
    private void loading() {
        gl.clear();
        
        String text = Messages.getString("Renderer.0");        
        Dimension screenSize = gl.getSize();        
        TextRenderer textRenderer = new TextRenderer(new Font(Font.SANS_SERIF, Font.BOLD, 16), 
                true, false);        
        textRenderer.beginRendering(screenSize.width, screenSize.height);
        Dimension textSize = textRenderer.getBounds(text).getBounds().getSize();
        textRenderer.draw(text, (screenSize.width - textSize.width) / 2,
                (screenSize.height - textSize.height) / 2);
        textRenderer.endRendering();
    }

    private Matrix4 correctGLUTransformation(Matrix4 matrix) {
        matrix = matrix.clone();
        matrix.rotate(new Vector3(-1, 0, 0), PI / 2);
        return matrix;
    }    

    private void render() {
        gl.clear();
        gl.loadMatrix(GL_PROJECTION, camera.getProjectionMatrix());

        Matrix4 skyMatrix = correctGLUTransformation(camera.getSkyViewMatrix());
        gl.loadMatrix(GL_MODELVIEW, skyMatrix);
        gl.drawSphere(50, 15, 8, true, sky);       
        
        if (activeDisplayMode == DisplayMode.SOLAR_SYSTEM) {
            
            gl.placeLight(0, new Vector3(0, -50, 0));
            gl.setFeatureEnabled(GL_LIGHTING, true);
            
            Matrix4 modelMatrix = camera.getModelViewMatrix().clone();
            gl.loadMatrix(GL_MODELVIEW, correctGLUTransformation(modelMatrix));
            gl.drawSphere(1, 60, 40, false, earth);
            
            modelMatrix.translate(-4, 0, 0);
            gl.loadMatrix(GL_MODELVIEW, correctGLUTransformation(modelMatrix));
            gl.drawSphere(0.2, 30, 20, false, moon);
            
            gl.setFeatureEnabled(GL_LIGHTING, false);
            
        } else {
            
            gl.loadMatrix(GL_MODELVIEW, camera.getModelViewMatrix());

            Iterable<Tile> tiles = CameraUtils.getVisibleTiles(camera, 
                    mapConfiguration.getOptimalTileLayout(camera, screenSize));
            for (Tile tile : tiles) {
                Texture texture = textureManager.getTexture(tile);
                VertexBuffer vbo = tileMeshManager.requestObject(tile, null).value;
                gl.drawVertexBuffer(vbo, texture);
            }

            gl.loadMatrix(GL_PROJECTION, new Matrix4());
            gl.loadMatrix(GL_MODELVIEW, new Matrix4());
            
            //TextRenderer textRenderer = new TextRenderer(new Font(Font.SANS_SERIF, 0, 10));
            //textRenderer.beginRendering(screenSize.width, screenSize.height);
            
            double xOffset = (double) ICON_SIZE / screenSize.width / 2,
                   yOffset = (double) ICON_SIZE / screenSize.height / 2;
            
                        
            Collection<Location> locations = locationManager.getActiveLocations(tiles);
            
            //Collection<Location> locations = new ArrayList<>();
            //locations.add(new Location(new GeoCoordinates(0, 0), LocationType.BANK, null, null));
            
            gl.setFeatureEnabled(GL_DEPTH_TEST, false);
            
                        
            
            for (Location location : locations) {
                if (location.point != null && camera.isPointVisible(location.point)) {
                    Texture overlayTexture = overlayIconTextures.get(location.type);
                    ScreenCoordinates center = camera.getScreenCoordinates(location.point);
                    if (overlayTexture != null) {
                        ScreenCoordinates
                            upperLeft = new ScreenCoordinates(center.x - xOffset, center.y - yOffset),
                            lowerRight = new ScreenCoordinates(center.x + xOffset, center.y + yOffset);
                        
                        gl.drawRectangle(upperLeft, lowerRight, overlayTexture);
                    }
                    
                    /*if (location.name != null && (location.type == LocationType.CITY 
                            || location.type == LocationType.TOWN 
                            || location.type == LocationType.VILLAGE)) {
                        String text = location.name;
                        Dimension textSize = textRenderer.getBounds(text).getBounds().getSize();                    
                        textRenderer.draw(text, (int)(center.x * screenSize.width) + ICON_SIZE / 2 + 4, 
                                (int)(center.y * screenSize.width) - textSize.height);
                    }*/
                }
            }
            gl.setFeatureEnabled(GL_DEPTH_TEST, true);
            //textRenderer.endRendering();
        }
    }
    
    public Camera getCamera() {
        return camera;
    }

    private void initialize() {        
        gl.setLightEnabled(0, true);
        gl.setLightIntensity(0, 1);
        gl.setAmbientLight(0.2);
        gl.setMaterialSpecularity(0.02);

        textureManager = new TextureManager(gl, 500, mapConfiguration);
        textureManager.addSurfaceListener(new SurfaceValidator());

        loadTextures();

        Settings.getInstance().addSettingsListener(SettingsContract.LEVEL_OF_DETAIL,
                settingsListener);
        Settings.getInstance().addSettingsListener(SettingsContract.TEXTURE_FILTER,
                settingsListener);
        
        tileMeshManager = new VertexBufferManager(gl, null);
        tileMeshManager.setTileSubdivisions(tileSubdivisions);
        applyDisplayMode();
    }
    
    
    private void dispose() {
        if (textureManager != null)
            textureManager.dispose();
        textureManager = null;
        if (tileMeshManager != null)
            tileMeshManager.dispose();
        tileMeshManager = null;

        Settings.getInstance().removeSettingsListener(SettingsContract.TEXTURE_FILTER, 
                settingsListener);
        Settings.getInstance().removeSettingsListener(SettingsContract.LEVEL_OF_DETAIL, 
                settingsListener);
        
        freeTextures();
    }
    

    private void loadTextures() {
        TextureFilter textureFilter = TextureFilter.valueOf(Settings.getInstance().getString(
                SettingsContract.TEXTURE_FILTER));

        earth = gl.loadTexture(Resource.loadTextureData("textures/earth.jpg", "jpg"),
                textureFilter);
        moon = gl.loadTexture(Resource.loadTextureData("textures/moon.jpg", "jpg"), textureFilter);
        sky = gl.loadTexture(Resource.loadTextureData("textures/sky.jpg", "jpg"), textureFilter);

        overlayIconTextures = new LinkedHashMap<>();
        for (LocationType key : LocationType.values()) {
            String resourceName = "locationIcons/" + key.toString() + ".png";
            if (Resource.exists(resourceName)) {
                Texture value = gl.loadTexture(Resource.loadTextureData(resourceName, "png"), 
                        textureFilter);
                overlayIconTextures.put(key, value);
            }
        }
    }
    
    
    private void freeTextures() {
        for (Texture texture : overlayIconTextures.values()) {
            gl.deleteTexture(texture);
        }
        gl.deleteTexture(sky);
        gl.deleteTexture(moon);
        gl.deleteTexture(earth);
        overlayIconTextures = null;
    }



    private class GraphicsSettingsListener implements SettingsListener {

        @Override
        public void settingsChanged(String key, Object valOld, Object valNew) {
            if (key.equals(SettingsContract.LEVEL_OF_DETAIL)) {
                setLevelOfDetail(Enum.valueOf(LevelOfDetail.class, (String) valNew));
            } else if (key.equals(SettingsContract.TEXTURE_FILTER)) {
                reloadTextures();
            }
        }
        
        private void reloadTextures() {
            gl.invokeLater(new Runnable() {
                
                @Override
                public void run() {
                    freeTextures();
                    loadTextures();
                    // TODO invokeLater() should already cause a redisplay. 
                    // Why is this insufficient?
                    gl.postRedisplay();
                }
            });
        }

        private void setLevelOfDetail(LevelOfDetail lod) {
            synchronized (Renderer.this) {
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
            gl.postRedisplay();
        }

    }

    /*
     * A Listener to handle events for OpenGL rendering.
     */
    private class RendererGLListener implements GLContextListener {

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
            switch (initState) {
                case AWAITING:
                    initState = InitState.LOADING;
                    loading();
                    gl.postRedisplay();
                    break;
                    
                case LOADING:
                    initState = InitState.DONE;
                    Renderer.this.initialize();
                    // fall through
                    
                case DONE:
                    render();
            }
        }

        @Override
        public void dispose(GLContext context) {
            Renderer.this.dispose();
        }

        @Override
        public void initialize(GLContext context) {
            gl.setFeatureEnabled(GL_DEPTH_TEST, true);
            gl.setFeatureEnabled(GL_CULL_FACE, true);        
            gl.setFeatureEnabled(GL_TEXTURE_2D, true);
            gl.setFeatureEnabled(GL_BLEND, true);
                        
            initState = InitState.AWAITING;
        }

        @Override
        public void reshape(GLContext context, int width, int height) {
            screenSize = new Dimension(width, height);
            camera.setPerspective(PI / 2, (double) width/height, 1e-4, 100);
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
     * {@link de.joglearth.map.single.SingleMapType} as it is only one tile as a texture.
     * 
     * @param t The new <code>MapLayout</code>
     */
    public synchronized void setMapConfiguration(MapConfiguration configuration) {
        this.mapConfiguration = configuration;
        applyMapConfiguration();
    }

    private void applyMapConfiguration() {
        if (textureManager != null) {
            textureManager.setMapConfiguration(mapConfiguration);
        }
        gl.postRedisplay();
    }
    
    public void setHeightMap(HeightMap hm) {
        
    }

}
