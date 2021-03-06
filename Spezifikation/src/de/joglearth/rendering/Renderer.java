package de.joglearth.rendering;

import static java.lang.Math.*;
import static javax.media.opengl.GL2.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
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
import de.joglearth.opengl.GLContext;
import de.joglearth.opengl.GLContextListener;
import de.joglearth.opengl.TextureFilter;
import de.joglearth.opengl.TransformedTexture;
import de.joglearth.opengl.VertexBuffer;
import de.joglearth.settings.Settings;
import de.joglearth.settings.SettingsContract;
import de.joglearth.settings.SettingsListener;
import de.joglearth.source.Priorized;
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
    private double solarSystemEarthRotation = 0, solarSystemMoonRevolution = 0;
    private Texture[][] bubble = new Texture[3][3];
    

    private enum InitState {
        AWAITING,
        LOADING,
        DONE
    }


    private InitState initState;
    private HeightMap heightMap = FlatHeightMap.getInstance();
    private TextRenderer locationTextRenderer;
    private TextRenderer bubbleCaptionRenderer;
    private TextRenderer bubbleTextRenderer;


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
        return matrix.rotate(new Vector3(-1, 0, 0), PI / 2);
    }
    

    private void drawOutlinedText(TextRenderer tr, int x, int y, String text, double fborder) {
        int border = (int) ceil(fborder);
        for (int xoff = -border; xoff <= border; ++xoff) {
            for (int yoff = -border; yoff <= border; ++yoff) {
                if (xoff != 0 || yoff != 0) {
                    double shade = exp(-(2/fborder)*(max(abs(xoff), max(abs(yoff), 1)) - 1));
                    tr.setColor(new Color(255, 255, 255, (int) (255*shade)));
                    tr.draw(text,  x+xoff, y+yoff);
                }
            }
        }
        tr.setColor(Color.BLACK);
        tr.draw(text, x, y);
        tr.setColor(Color.WHITE);
    }
    
    
    private ScreenCoordinates sizeToScreenCoordinates(int x, int y) {
        return new ScreenCoordinates((double) x / screenSize.width, 
                (double) y / screenSize.height);
    }
    
    
    private void drawRectangleUpLeft(double right, double bottom, ScreenCoordinates size, 
            Texture texture) {
        double left = right - size.x, top = bottom - size.y;
        if (left >= 0 && left <= 1 && top >= 0 && top <= 1 && right >= 0 && right <= 1
                && bottom >= 0 && bottom <= 1) {
            gl.drawRectangle(new ScreenCoordinates(left, top),
                    new ScreenCoordinates(right, bottom), texture);  
        }
    }
    
    
    private void drawBubble(ScreenCoordinates origin, String caption, String[] text) {
        Rectangle2D captionSize = bubbleCaptionRenderer.getBounds(caption);
        Rectangle2D[] textSizes = new Rectangle2D[text.length];
        int width = 25, height = (int) captionSize.getHeight() + 1;
       
        for (int i=0; i<text.length; ++i) {
            textSizes[i] = bubbleTextRenderer.getBounds(text[i]);
            width = max(width, (int) textSizes[i].getWidth());
            height += 5 + (int) textSizes[i].getHeight();
        }        
        
        ScreenCoordinates 
            size = sizeToScreenCoordinates(bubble[2][2].getWidth(), bubble[2][2].getHeight());
        double right = origin.x, bottom = origin.y;
        
        drawRectangleUpLeft(right, bottom, size, bubble[2][2]);
        right -= size.x;
        size = sizeToScreenCoordinates(
                width - (bubble[2][2].getWidth() - bubble[0][2].getWidth()), 
                bubble[2][1].getHeight());
        drawRectangleUpLeft(right, bottom, size, bubble[2][1]);
        right -= size.x;
        size = sizeToScreenCoordinates(bubble[2][0].getWidth(), bubble[2][0].getHeight());
        drawRectangleUpLeft(right, bottom, size, bubble[2][0]);      
        
        right = origin.x;
        bottom = origin.y - size.y;
        size = sizeToScreenCoordinates(bubble[1][2].getWidth(), height);        
        drawRectangleUpLeft(right, bottom, size, bubble[1][2]);      
        right -= size.x;
        size = sizeToScreenCoordinates(width, height);
        drawRectangleUpLeft(right, bottom, size, bubble[1][1]);
        right -= size.x;
        size = sizeToScreenCoordinates(bubble[1][0].getWidth(), height);
        drawRectangleUpLeft(right, bottom, size, bubble[1][0]);      

        right = origin.x;
        bottom -= size.y;
        size = sizeToScreenCoordinates(bubble[0][2].getWidth(), bubble[0][2].getHeight());        
        drawRectangleUpLeft(right, bottom, size, bubble[0][2]);   
        right -= size.x;
        size = sizeToScreenCoordinates(width, bubble[0][1].getHeight());
        drawRectangleUpLeft(right, bottom, size, bubble[0][1]);  
        right -= size.x;
        size = sizeToScreenCoordinates(bubble[0][0].getWidth(), bubble[0][0].getHeight());
        drawRectangleUpLeft(right, bottom, size, bubble[0][0]);      

        Point textOrigin = new Point((int) (right * screenSize.width), 
                                     (int) (bottom * screenSize.height));

        bubbleCaptionRenderer.beginRendering(screenSize.width, screenSize.height);
        bubbleCaptionRenderer.setColor(Color.BLACK);
        bubbleCaptionRenderer.draw(caption, textOrigin.x,
                screenSize.height - textOrigin.y - (int) captionSize.getHeight());
        bubbleCaptionRenderer.setColor(Color.WHITE);
        bubbleCaptionRenderer.endRendering();
        
        bubbleTextRenderer.beginRendering(screenSize.width, screenSize.height);
        bubbleTextRenderer.setColor(Color.BLACK);
        int textY =  screenSize.height - textOrigin.y - (int) captionSize.getHeight() - 1;
        for (int i=0; i<text.length; ++i) {
            textY -= textSizes[i].getHeight() + 5;
            bubbleTextRenderer.draw(text[i] , textOrigin.x, textY);
        }
        bubbleTextRenderer.setColor(Color.WHITE);
        bubbleTextRenderer.endRendering();       
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
            
            solarSystemEarthRotation += 0.002;
            solarSystemMoonRevolution += 0.0005;

            // The sky is drawn close to the camera to avoid clipping, but is always in the background
            gl.setFeatureEnabled(GL_DEPTH_TEST, true);
            gl.clear(GL_DEPTH_BUFFER_BIT);

            gl.setAmbientLight(0.2);
            
            gl.placeLight(0, new Vector4(0, -5, 0, 0));
            gl.setFeatureEnabled(GL_LIGHTING, true);
            
            Matrix4 earthMatrix = camera.getModelViewMatrix()
                    .rotate(new Vector3(1, 0, 0), 23.0*PI/180.0)
                    .rotate(new Vector3(0, 1, 0), solarSystemEarthRotation);
            earthMatrix = correctGLUTransformation(earthMatrix);
            gl.loadMatrix(GL_MODELVIEW, earthMatrix);
            gl.drawSphere(1, 60, 40, false, earth);

            Matrix4 moonMatrix = camera.getModelViewMatrix()
                    .rotate(new Vector3(0, 1, 0), solarSystemMoonRevolution)
                    .translate(-3, 0, 0);
            gl.loadMatrix(GL_MODELVIEW, correctGLUTransformation(moonMatrix));
            gl.drawSphere(0.2, 30, 20, false, moon);

            gl.setFeatureEnabled(GL_LIGHTING, false);

        } else {
            
            textureManager.increasePriority();
            locationManager.increasePriority();        
            if (heightMap instanceof Priorized) {
                ((Priorized) heightMap).increasePriority();
            }
            
            // Blend inner day sky
            gl.setBlendingFunction(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            gl.setFeatureEnabled(GL_BLEND, true);
            float daySkyAlpha = (float) min(0.8, max(0.0, pow(camera.getDistance(), -2) / 1000 ));
            float[] daySkyColor = { 0.2734375f, 0.5484375f, 1, daySkyAlpha };
            gl.drawSphere(zNear * 10, 15, 8, true, daySkyColor);
            gl.setFeatureEnabled(GL_BLEND, false);

            gl.setAmbientLight(1.0);
            
            gl.loadMatrix(GL_MODELVIEW, Matrix4.IDENTITY);
            gl.placeLight(0, new Vector4(-camera.getDistance(), 0, 0, 1));
            gl.setFeatureEnabled(GL_LIGHTING, true);
            
            gl.loadMatrix(GL_MODELVIEW, camera.getModelViewMatrix());
            
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
                TransformedTexture texture = textureManager.getTexture(tile, 0);
                    gl.loadMatrix(GL_TEXTURE, texture.transformation);
                    
                HeightMap effectiveHeightMap = (camera.getSurfaceScale() < 0.005 && scaleDown <= 2 )
                        ? heightMap : FlatHeightMap.getInstance();

                // tsb.append(texture.getTextureObject());
                // tsb.append(", ");
                ProjectedTile projected = new ProjectedTile(tile, mapConfiguration.getProjection(),
                        minEquatorSubdivisions, equatorSubdivisions, effectiveHeightMap);
                VertexBuffer vbo = tileMeshManager.requestObject(projected, null).value;
                gl.drawVertexBuffer(vbo, texture != null ? texture.texture : null);
            }
            
            gl.setFeatureEnabled(GL_LIGHTING, false);
            
            gl.loadMatrix(GL_PROJECTION, Matrix4.IDENTITY);
            gl.loadMatrix(GL_MODELVIEW, Matrix4.IDENTITY);
            gl.loadMatrix(GL_TEXTURE, Matrix4.IDENTITY);            


            double xOffset = (double) ICON_SIZE / screenSize.width / 2, yOffset = (double) ICON_SIZE
                    / screenSize.height / 2;

            Collection<Location> locations = locationManager.getActiveLocations(tiles);;

            gl.setFeatureEnabled(GL_DEPTH_TEST, false);
            gl.setBlendingFunction(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            gl.setFeatureEnabled(GL_BLEND, true);

            for (Location location : locations) {
                if (location.point != null && camera.isPointVisible(location.point)) {
                    Texture overlayTexture = overlayIconTextures.get(location.type);
                    ScreenCoordinates center = camera.getScreenCoordinates(location.point);
                    if (center != null && overlayTexture != null) {
                        double left = center.x - xOffset, top = center.y - yOffset,
                               right = center.x + xOffset, bottom = center.y + yOffset;
                        
                        if (left >= 0 && top >= 0 && right <= 1 && bottom <= 1) {
                            ScreenCoordinates upperLeft = new ScreenCoordinates(left, top), 
                                    lowerRight = new ScreenCoordinates(right, bottom);
    
                            gl.drawRectangle(upperLeft, lowerRight, overlayTexture);
                        }
                    }
                }
            }
            
            locationTextRenderer.beginRendering(screenSize.width, screenSize.height);

            for (Location location : locations) {
                if (location.point != null && camera.isPointVisible(location.point)) {
                    ScreenCoordinates center = camera.getScreenCoordinates(location.point);
                    if (center != null && location.name != null 
                            && (location.type == LocationType.USER_TAG 
                            || location.type == LocationType.SEARCH_RESULT 
                            || location.type == LocationType.CITY
                            || location.type == LocationType.TOWN
                            || location.type == LocationType.VILLAGE))
                    {
                        String text = location.name;
                        Dimension textSize =
                                locationTextRenderer.getBounds(text).getBounds().getSize();
                        drawOutlinedText(locationTextRenderer,
                                (int) (center.x * screenSize.width) + ICON_SIZE / 2 + 4,
                                screenSize.height - ((int) (center.y * screenSize.height) + textSize.height / 2),
                                text, 1.2);
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

        textureManager = new TextureManager(gl, 1500, mapConfiguration);
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

        locationTextRenderer = new TextRenderer(new Font(Font.SANS_SERIF, Font.BOLD, 12), true, 
                false);        
        bubbleCaptionRenderer = new TextRenderer(new Font(Font.SANS_SERIF, Font.BOLD, 12), true, 
                false);        
        bubbleTextRenderer = new TextRenderer(new Font(Font.SANS_SERIF, 0, 12), true, 
                false);
        
        camera.setPosition(new GeoCoordinates(30*PI/180, 15*PI/180));        
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
            try {
                TextureData data = gl.loadTextureData(rsrcStream, extension);
                if (data == null) {
                    rsrcStream.close();
                    rsrcStream = Resource.open(name);
                    data = gl.loadTextureDataScaled(rsrcStream, extension);
                }
                return gl.loadTexture(data, textureFilter);
            } finally {
                rsrcStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadTextures() {
        TextureFilter textureFilter = TextureFilter.valueOf(Settings.getInstance().getString(
                SettingsContract.TEXTURE_FILTER));

        earth = loadTextureResource("textures/earth.jpg", "jpg", textureFilter);
        moon = loadTextureResource("textures/moon.jpg", "jpg", textureFilter);
        nightSky = loadTextureResource("textures/sky.png", "png", textureFilter);
        crosshair = loadTextureResource("icons/crosshair.png", "png", textureFilter);
        
        overlayIconTextures = new LinkedHashMap<>();
        for (LocationType key : LocationType.values()) {
            String resourceName = "locationIcons/" + key.toString() + ".png";
            if (Resource.exists(resourceName)) {
                Texture value;
                value = loadTextureResource(resourceName, "png", TextureFilter.TRILINEAR);
                overlayIconTextures.put(key, value);                    
            }
        }
        
        for (int vert = 0; vert < 3; ++vert) {
            for (int horz = 0; horz < 3; ++horz) {
                String vString = vert == 0 ? "top" : vert == 1 ? "middle" : "bottom",
                       hString = horz == 0 ? "Left" : horz == 1 ? "Center" : "Right";  
                
                bubble[vert][horz] = loadTextureResource("bubble/" + vString + hString + ".png",
                        "png", TextureFilter.NEAREST);
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
            //gl.setPolygonMode(GL_LINE);
            
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
        
        if (activeDisplayMode == DisplayMode.SOLAR_SYSTEM) {
            gl.startDisplayLoop();
        } else {
            gl.stopDisplayLoop();
            gl.postRedisplay();
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
