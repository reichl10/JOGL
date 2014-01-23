package de.joglearth.rendering;

import static java.lang.Math.*;
import static javax.media.opengl.GL2.*;

import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;

import de.joglearth.geometry.Camera;
import de.joglearth.geometry.CameraListener;
import de.joglearth.geometry.CameraUtils;
import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.Matrix4;
import de.joglearth.geometry.PlaneGeometry;
import de.joglearth.geometry.ScreenCoordinates;
import de.joglearth.geometry.SimpleTile;
import de.joglearth.geometry.SphereGeometry;
import de.joglearth.geometry.SurfaceListener;
import de.joglearth.geometry.Tile;
import de.joglearth.geometry.TileLayout;
import de.joglearth.geometry.Vector3;
import de.joglearth.geometry.Vector4;
import de.joglearth.height.HeightMap;
import de.joglearth.height.flat.FlatHeightMap;
import de.joglearth.location.Location;
import de.joglearth.location.LocationManager;
import de.joglearth.location.LocationType;
import de.joglearth.map.MapConfiguration;
import de.joglearth.map.osm.OSMMapConfiguration;
import de.joglearth.map.osm.OSMMapType;
import de.joglearth.map.single.SingleMapConfiguration;
import de.joglearth.map.single.SingleMapType;
import de.joglearth.opengl.GLContext;
import de.joglearth.opengl.GLContextListener;
import de.joglearth.opengl.TextureFilter;
import de.joglearth.opengl.TransformedTexture;
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
    private int subdivisionPixels = 1;
    private LocationManager locationManager;
    private TextureManager textureManager;
    private Camera camera;
    private DisplayMode activeDisplayMode = DisplayMode.SOLAR_SYSTEM;
    private Texture earth, moon, nightSky, crosshair;
    private VertexBufferManager tileMeshManager;
    private SurfaceListener surfaceListener = new SurfaceValidator();
    private GLContextListener glContextListener = new RendererGLListener();
    private Map<LocationType, Texture> overlayIconTextures;
    private SettingsListener settingsListener = new GraphicsSettingsListener();
    private MapConfiguration mapConfiguration = new OSMMapConfiguration(OSMMapType.SATELLITE);
    private Dimension screenSize = new Dimension(640, 480);
    private final static int ICON_SIZE = 24;
    private final static double FOV = PI / 2;
    private double aspectRatio = 1;


    private enum InitState {
        AWAITING,
        LOADING,
        DONE
    }


    private InitState initState;
    private HeightMap heightMap = FlatHeightMap.getInstance();
    private LevelOfDetail levelOfDetail;
    private TextRenderer locationTextRenderer;


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

    /**
     * Sets the {@link GLContext} to a given value.
     * 
     * @param gl The new {@link GLContext}
     */
    public void setGLContext(GLContext gl) {
        if (gl == null) {
            throw new IllegalArgumentException();
        }

        this.gl = gl;
        gl.addGLContextListener(glContextListener);
    }

    /**
     * Sets the {@link LocationManager} to a given value.
     * 
     * @param manager The new {@link LocationManager}
     */
    public void setLocationManager(LocationManager manager) {
        if (manager == null) {
            throw new IllegalArgumentException();
        }

        this.locationManager = manager;
        locationManager.addSurfaceListener(surfaceListener);
    }

    private void loading() {
        gl.clear(GL_COLOR_BUFFER_BIT);
        gl.setFeatureEnabled(GL_DEPTH_TEST, false);        

        String text = Messages.getString("Renderer.0");
        Dimension screenSize = gl.getSize();
        TextRenderer textRenderer = new TextRenderer(new Font(Font.SANS_SERIF, Font.BOLD, 16),
                true, false);
        textRenderer.beginRendering(screenSize.width, screenSize.height);
        Dimension textSize = textRenderer.getBounds(text).getBounds().getSize();
        textRenderer.draw(text, (screenSize.width - textSize.width) / 2,
                (screenSize.height - textSize.height) / 2);
        textRenderer.endRendering();
        
        gl.setFeatureEnabled(GL_DEPTH_TEST, true);        
    }

    private Matrix4 correctGLUTransformation(Matrix4 matrix) {
        matrix = matrix.clone();
        matrix.rotate(new Vector3(-1, 0, 0), PI / 2);
        return matrix;
    }

    private void render() {
        // Construct projection matrix based on the distance to avoid clipping. Constants cause
        // problem with graphics adapters that don't support a 24 bit depth buffer.
        double zNear = camera.getDistance() / 10, zFar = zNear * 2000;
        camera.setPerspective(FOV, aspectRatio, zNear, zFar);
        gl.loadMatrix(GL_PROJECTION, camera.getProjectionMatrix());
        
        // Depth buffer is cleared later
        gl.clear(GL_COLOR_BUFFER_BIT);
        gl.setFeatureEnabled(GL_DEPTH_TEST, false);        
        
        // Draw outer night sky. Depth-testing and -buffering is disabled, so radius must just be
        // larger than zNear.        
        Matrix4 skyMatrix = correctGLUTransformation(camera.getSkyViewMatrix());
        gl.loadMatrix(GL_MODELVIEW, skyMatrix);
        gl.drawSphere(zNear * 10, 15, 8, true, nightSky);        
        
        if (activeDisplayMode == DisplayMode.SOLAR_SYSTEM) {

            // The sky is drawn close to the camera to avoid clipping, but is always in the background
            gl.setFeatureEnabled(GL_DEPTH_TEST, true);
            gl.clear(GL_DEPTH_BUFFER_BIT);

            gl.setAmbientLight(0.2);
            
            gl.placeLight(0, new Vector4(0, -5, 0, 0));
            gl.setFeatureEnabled(GL_LIGHTING, true);

            Matrix4 modelMatrix = camera.getModelViewMatrix().clone();
            gl.loadMatrix(GL_MODELVIEW, correctGLUTransformation(modelMatrix));
            gl.drawSphere(1, 60, 40, false, earth);

            modelMatrix.translate(-4, 0, 0);
            gl.loadMatrix(GL_MODELVIEW, correctGLUTransformation(modelMatrix));
            gl.drawSphere(0.2, 30, 20, false, moon);

            gl.setFeatureEnabled(GL_LIGHTING, false);

        } else {
            
            // Blend inner day sky
            gl.setBlendingFunction(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            gl.setFeatureEnabled(GL_BLEND, true);
            float daySkyAlpha = (float) min(0.8, max(0.0, pow(camera.getDistance(), -2) / 1000 ));
            float[] daySkyColor = { 0.2734375f, 0.5484375f, 1, daySkyAlpha };
            gl.drawSphere(zNear * 10, 15, 8, true, daySkyColor);
            gl.setFeatureEnabled(GL_BLEND, false);

            gl.setAmbientLight(1.0);
            
            gl.loadMatrix(GL_MODELVIEW, new Matrix4());
            gl.placeLight(0, new Vector4(-camera.getDistance(), 0, 0, 1));
            gl.setFeatureEnabled(GL_LIGHTING, true);
            
            gl.loadMatrix(GL_MODELVIEW, camera.getModelViewMatrix());
            /*
            int slices = 0;
            switch (levelOfDetail) {
                case LOW: slices = 100; break;
                case MEDIUM: slices = 250; break;
                case HIGH: slices = 750; break;
            }
            
            gl.drawSphere(1, slices, slices / 2, false, new float[] {0.8f, 0.8f, 0.8f, 1});*/
            
            // The sky is drawn close to the camera to avoid clipping, but is always in the background
            gl.setFeatureEnabled(GL_DEPTH_TEST, true);
            gl.clear(GL_DEPTH_BUFFER_BIT);

            TileLayout layout = mapConfiguration.getOptimalTileLayout(camera, screenSize);
            int equatorSubdivisions = (1 << max(0, (int) ceil(log((double) screenSize.width
                    / subdivisionPixels / camera.getSurfaceScale())
                    / log(2)))), minEquatorSubdivisions = layout.getHoritzontalTileCount();
            Iterable<Tile> tiles = CameraUtils.getVisibleTiles(camera, layout, 500);
            
            for (Tile tile : tiles) {
                int scaleDown = (int) abs(camera.getSpacePosition(
                        new GeoCoordinates(tile.getLongitudeFrom(), tile.getLatitudeFrom()))
                        .to(camera.getSpacePosition(camera.getPosition())).length()
                        / camera.getDistance() / 2);
                TransformedTexture texture = null;
                if (scaleDown <= 2) {
                    texture = textureManager.getTexture(tile, scaleDown);
                    gl.loadMatrix(GL_TEXTURE, texture.transformation);
                }
                HeightMap effectiveHeightMap = (camera.getSurfaceScale() < 0.005 && scaleDown <= 2 )? heightMap
                        : FlatHeightMap.getInstance();

                // tsb.append(texture.getTextureObject());
                // tsb.append(", ");
                ProjectedTile projected = new ProjectedTile(tile, mapConfiguration.getProjection(),
                        minEquatorSubdivisions, equatorSubdivisions, effectiveHeightMap);
                VertexBuffer vbo = tileMeshManager.requestObject(projected, null).value;
                gl.drawVertexBuffer(vbo, texture != null ? texture.texture : null);
            }
            
            gl.setFeatureEnabled(GL_LIGHTING, false);
            
            gl.loadMatrix(GL_PROJECTION, new Matrix4());
            gl.loadMatrix(GL_MODELVIEW, new Matrix4());
            gl.loadMatrix(GL_TEXTURE, new Matrix4());            


            double xOffset = (double) ICON_SIZE / screenSize.width / 2, yOffset = (double) ICON_SIZE
                    / screenSize.height / 2;

            Collection<Location> locations = locationManager.getActiveLocations(tiles);;
            // Collection<Location> locations = new ArrayList<>();
            // locations.add(new Location(new GeoCoordinates(0, 0), LocationType.BANK, null, null));

            gl.setFeatureEnabled(GL_DEPTH_TEST, false);
            gl.setBlendingFunction(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            gl.setFeatureEnabled(GL_BLEND, true);

            for (Location location : locations) {
                if (location.point != null && camera.isPointVisible(location.point)) {
                    Texture overlayTexture = overlayIconTextures.get(location.type);
                    ScreenCoordinates center = camera.getScreenCoordinates(location.point);
                    if (overlayTexture != null) {
                        ScreenCoordinates upperLeft = new ScreenCoordinates(center.x - xOffset,
                                center.y - yOffset), lowerRight = new ScreenCoordinates(center.x
                                + xOffset, center.y + yOffset);

                        gl.drawRectangle(upperLeft, lowerRight, overlayTexture);
                    }
                }
            }
            
            locationTextRenderer.beginRendering(screenSize.width, screenSize.height);

            for (Location location : locations) {
                if (location.point != null && camera.isPointVisible(location.point)) {
                    ScreenCoordinates center = camera.getScreenCoordinates(location.point);
                    if (location.name != null)
                    {
                        String text = location.name;
                        Dimension textSize =
                                locationTextRenderer.getBounds(text).getBounds().getSize();
                        locationTextRenderer.draw(text,
                                (int) (center.x * screenSize.width) + ICON_SIZE / 2 + 4,
                                (int) (center.y * screenSize.height) - textSize.height);
                    }

                }
            }
            
            locationTextRenderer.endRendering();

            gl.drawRectangle(new ScreenCoordinates(0.5 - xOffset, 0.5 - yOffset),
                    new ScreenCoordinates(0.5 + xOffset, 0.5 + yOffset), crosshair);

            gl.setFeatureEnabled(GL_BLEND, false);
        }
    }

    /**
     * Returns the current {@link Camera}.
     * 
     * @return The {@link Camera}
     */
    public Camera getCamera() {
        return camera;
    }

    private void initialize() {
        gl.setLightEnabled(0, true);
        gl.setLightIntensity(0, 1);
        gl.setMaterialSpecularity(0.02);

        textureManager = new TextureManager(gl, 500, mapConfiguration);
        textureManager.addSurfaceListener(new SurfaceValidator());

        loadTextures();

        Settings.getInstance().addSettingsListener(SettingsContract.LEVEL_OF_DETAIL,
                settingsListener);
        Settings.getInstance().addSettingsListener(SettingsContract.TEXTURE_FILTER,
                settingsListener);

        tileMeshManager = new VertexBufferManager(gl, null);
        applyDisplayMode();
        String lvlOfDetailsString = Settings.getInstance().getString(
                SettingsContract.LEVEL_OF_DETAIL);
        LevelOfDetail lod = LevelOfDetail.valueOf(lvlOfDetailsString);
        setLevelOfDetail(lod);
        
        gl.setBlendingFunction(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        locationTextRenderer = new TextRenderer(new Font(Font.SANS_SERIF, Font.BOLD, 12));
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
    
    private Texture loadTextureResource(String name, String extension, TextureFilter textureFilter)
    {
        try {
            InputStream rsrcStream = Resource.open(name);
            TextureData data = gl.loadTextureData(rsrcStream, extension);
            return gl.loadTexture(data, textureFilter);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadTextures() {
        TextureFilter textureFilter = TextureFilter.valueOf(Settings.getInstance().getString(
                SettingsContract.TEXTURE_FILTER));

        earth = loadTextureResource("textures/earth.png", "png", textureFilter);
        moon = loadTextureResource("textures/moon.jpg", "jpg", textureFilter);
        nightSky = loadTextureResource("textures/sky.png", "png", textureFilter);
        crosshair = loadTextureResource("icons/crosshair.png", "png", textureFilter);
        
            overlayIconTextures = new LinkedHashMap<>();
            for (LocationType key : LocationType.values()) {
                String resourceName = "locationIcons/" + key.toString() + ".png";
                System.err.println(key.name() + " Texture loading...!");
                if (Resource.exists(resourceName)) {
                    Texture value;
                    value = loadTextureResource(resourceName, "png", TextureFilter.TRILINEAR);
                    System.err.println(key.name() + " Texure loaded!");
                    overlayIconTextures.put(key, value);                    
                }
            }
    }

    private void freeTextures() {
        if (overlayIconTextures != null)
            for (Texture texture : overlayIconTextures.values()) {
                gl.deleteTexture(texture);
            }
        if (nightSky != null)
            gl.deleteTexture(nightSky);
        if (moon != null)
            gl.deleteTexture(moon);
        if (earth != null)
            gl.deleteTexture(earth);
        overlayIconTextures = null;
    }

    private void setLevelOfDetail(LevelOfDetail lod) {
        synchronized (this) {
            switch (lod) {
                case LOW:
                    subdivisionPixels = 100;
                    break;
                case MEDIUM:
                    subdivisionPixels = 45;
                    break;
                case HIGH:
                    subdivisionPixels = 20;
            }
            this.levelOfDetail = lod;
        }
        gl.postRedisplay();
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
//            gl.setPolygonMode(GL_LINE);
            
            initState = InitState.AWAITING;
        }

        @Override
        public void reshape(GLContext context, int width, int height) {
            screenSize = new Dimension(width, height);
            aspectRatio = (double) width / height;
        }
    }

    private class SurfaceValidator implements SurfaceListener {

        @Override
        public void surfaceChanged(double lonFrom, double latFrom, double lonTo, double latTo) {
            boolean visible = true;
            GeoCoordinates[] edges = { new GeoCoordinates(lonFrom, latFrom),
                    new GeoCoordinates(lonFrom, latTo),
                    new GeoCoordinates(lonTo, latFrom),
                    new GeoCoordinates(lonTo, latTo) };
            for (GeoCoordinates geo : edges) {
                if (camera.isPointVisible(geo)) {
                    visible = true;
                }
            }
            if (!visible) {
                Tile tile = new SimpleTile(lonFrom, latFrom, lonTo, latTo);
                if (tile.contains(camera.getGeoCoordinates(new ScreenCoordinates(0.5, 0.5)))) {
                    visible = true;
                } 
            }
            
            if (visible) {
                gl.postRedisplay();
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
                if (tileMeshManager != null) {
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

    /**
     * Sets {@link HeightMap} for further tiles.
     * 
     * @param hm The HeighMap
     */
    public void setHeightMap(HeightMap hm) {
        if (hm == null) {
            throw new IllegalArgumentException();
        }
        
        if (!hm.equals(heightMap)) {
            heightMap = hm;
            camera.setHeightMap(hm);
            gl.postRedisplay();
        }
    }
}
