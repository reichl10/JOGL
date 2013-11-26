package de.joglearth.geometry;

import java.util.List;

import de.joglearth.geometry.Matrix4;
import de.joglearth.geometry.Vector3;
import de.joglearth.surface.HeightMap;
import de.joglearth.surface.SurfaceListener;


/**
 * Administers geometric calculations for the viewport perspective.
 * 
 * This class is not thread-safe by design, as clients will usually perform multiple dependent
 * operations on the camera. In multi-threaded code, these blocks of operations should be
 * synchronized on the camera object.
 */
public class Camera {

    private GeoCoordinates position;
    private double distance;
    private double tiltX;
    private double tiltY;
    private Matrix4 clipMatrix, projectionMatrix;
    private Geometry geometry;
    private List<CameraListener> listeners;
    
    
    private void notifyListeners() {
        
    }


    private void updateProjectionMatrix() {
        Matrix4 cameraMatrix = new Matrix4();
        cameraMatrix.rotateX(tiltX);
        cameraMatrix.rotateY(tiltY);
        projectionMatrix = cameraMatrix.inverse();
        projectionMatrix.mult(clipMatrix);
    }


    private class SurfaceHeightListener implements SurfaceListener {

        @Override
        public void surfaceChanged(double lonFrom, double latFrom, double lonTo, double latTo) {
            if (position.getLatitude() >= latFrom && position.getLatitude() <= latTo
                    && position.getLongitude() >= lonFrom && position.getLongitude() <= lonTo) {
                notifyListeners();
            }
        }
    }


    /**
     * Constructor.
     * 
     * Creates a camera with FOV 90, aspect ratio 1:1, zNear=0.1 and zFar=1000.
     * 
     * @param hm The height map manager.
     */
    public Camera() {
        setPerspective((double) Math.PI / 2, 1, 0.1f, 1000);
        HeightMap.addSurfaceListener(new SurfaceHeightListener());
    }

    /**
     * Sets a new geometry object for model-specific computations.
     * 
     * @param g The new geometry object.
     */
    public void setGeometry(Geometry g) {
        this.geometry = g;
    }

    /**
     * Sets the position the camera is currently over.
     * 
     * @param coords The surface coordinates of the camera's position.
     */
    public void setPosition(GeoCoordinates coords) {
        this.position = coords;
        updateProjectionMatrix();
    }

    /**
     * Sets the camera's distance to the surface.
     * 
     * @param distance The distance.
     */
    public void setDistance(double distance) {
        this.distance = distance;
        updateProjectionMatrix();
    }

    /**
     * Sets the parameters for the perspective transformation done by the projection matrix.
     * 
     * @param fov The field of view, in radians. This is the angular distance of the left and right
     *        clipping plane. 90° (PI/2) by default.
     * @param aspectRatio The aspect ratio, i.e. the width-to-height ratio. 1 by default.
     * @param near The distance of the near clipping plane to the camera position. 0.1 by default.
     * @param far The distance of the far clipping plane to the camera position. 1000.0 by default.
     */
    public void setPerspective(double fov, double aspectRatio, double near, double far) {
        double f = 1.f / (double) Math.tan(fov * 0.5f);
        double[] d = { f * aspectRatio, 0, 0, 0,
                0, f, 0, 0,
                0, 0, (far + near) / (far - near), 1,
                0, 0, (2.f * near * far) / (near - far), 0 };
        clipMatrix = new Matrix4(d);
    }

    /**
     * Resets the camera tilt to x=y=0.
     */
    public void resetTilt() {
        tiltX = 0;
        tiltY = 0;
        updateProjectionMatrix();
    }

    /**
     * Sets the camera tilt to a specific value.
     * 
     * @param x The tilt around the x axis ("up and down").
     * @param y The tilt around the y axis ("left and right").
     */
    public void setTilt(double x, double y) {

    }

    /**
     * Changes the tilt by a difference value.
     * 
     * @param deltaX The tilt difference around the x axis ("up and down").
     * @param deltaY The tilt difference around the y axis ("left and right").
     */
    public void tilt(double deltaX, double deltaY) {
        this.tiltX += deltaX;
        this.tiltY += deltaY;
        updateProjectionMatrix();
    }

    /**
     * Changes the camera's surface position by difference values.
     * 
     * @param deltaLon The angular distance to move in longitude direction.
     * @param deltaLat The angular distance to move in latitude direction.
     */
    public void move(double deltaLon, double deltaLat) {
        updateProjectionMatrix();
    }

    private boolean isPointVisisble(Vector3 point) {
        // Sichtbar, wenn: Transformierter Vektor in [0, 1] x [0, 1] x [0, inf]
        // und, falls Kugel, z <= Abstand zu (0, 0, 0) [?!]
        return false;
    }

    /**
     * Determines whether a surface point is visible by the camera. The visibility is limited by
     * both the viewport (the clipping planes) and parts of the scene closer to the camera that
     * might shadow others (the back of the globe, for example).
     * 
     * @param geo The coordinates to check for.
     * @return Whether the point is visible.
     */
    public boolean isPointVisible(GeoCoordinates geo) {
        return false;
    }

    /**
     * Calculates the screen coodrinates of a visible point given in longitude and latitude
     * coordinates. If the point is not visible, the result is unspecified.
     * 
     * @param geo The point's coordinates.
     * @return The coordinates of the point on the screen.
     */
    public ScreenCoordinates getScreenCoordinates(GeoCoordinates geo) {
        return null;
    }

    /**
     * Calculates the longitude and latitude coordinates of the point underneath a point on the
     * screen.
     * 
     * @param screen The screen coordinates.
     * @return The surface coordinates if the point maps to the surface, null if it points outside
     *         the plane or globe ("space").
     */
    public GeoCoordinates getGeoCoordinates(ScreenCoordinates screen) {
        return null;
    }

    /**
     * Returns an array of tiles visible or partially visible by the camera.
     * 
     * All tiles have the same detail level, which is calculated from the distance and number of
     * visible tiles.
     * 
     * @return The array of visible tiles.
     */
    public Tile[] getVisibleTiles() {
        return null;
    }

    /**
     * Returns the projection matrix derived from the current camera settings.
     * 
     * The result may be directly passed to OpenGL as a projection matrix, and is guaranteed to
     * produce a picture corresponding to whatever results the camera visibility and projection
     * methods produced for these settings.
     * 
     * @return The projection matrix.
     */
    public Matrix4 getProjectionMatrix() {
        return projectionMatrix;
    }

    public void addCameraListener(CameraListener l) {

    }

    public void removeCameraListener(CameraListener l) {

    }

}
