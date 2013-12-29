package de.joglearth.geometry;

import static java.lang.Double.isInfinite;
import static java.lang.Double.isNaN;
import static java.lang.Math.*;

import java.util.ArrayList;
import java.util.List;

import de.joglearth.surface.HeightMap;
import de.joglearth.surface.SurfaceListener;


/**
 * Administers geometric calculations for the viewport perspective.
 * 
 */
public class Camera {

    private GeoCoordinates position = new GeoCoordinates(0, 0);
    private double distance = 0.5;
    private double tiltX = 0;
    private double tiltY = 0;
    private double fov, aspectRatio;
    private boolean heightMapEnabled = false;
    private Matrix4 projectionMatrix,
                    modelCameraMatrix = new Matrix4(),
                    modelViewMatrix = new Matrix4(),
                    skyViewMatrix = new Matrix4(),
                    transformationMatrix;
    
    /* TODO reset visibility */public Geometry geometry = null;
    private List<CameraListener> listeners = new ArrayList<CameraListener>();

    
    private boolean updatesEnabled = false;
    
    public synchronized void setUpdatesEnabled(boolean enabled) {
        updatesEnabled = enabled;
    }
    
    private void updateCameraTransformation() {
        transformationMatrix = projectionMatrix.clone();
        transformationMatrix.mult(modelViewMatrix);
        if (updatesEnabled) {
            for (CameraListener l : listeners) {
                l.cameraViewChanged();
            }
        }
    }
    
    
    private boolean tiltTransformation(Matrix4 matrix) {
        Vector3 center = matrix.transform(new Vector3(0, 0, 0)).divide();
        Vector3 zAxis = matrix.transform(new Vector3(0, 0, -1)).divide().minus(center)
                .normalized();

        if (zAxis.x == 0 && zAxis.z == 0) {
            return false;
        }

        Vector3 earthAxis = matrix.transform(new Vector3(0, 1, 0)).divide().minus(center);
        Vector3 xAxis = zAxis.crossProduct(earthAxis).normalized();

        matrix.rotate(zAxis, -tiltY);
        matrix.rotate(xAxis, tiltX);
        
        return true;
    }
    

    private boolean updateCamera() {
        // TODO sign!
        // TODO Height map resolution is a wild guess
        
        double altitude = distance - HeightMap.MIN_HEIGHT;
        if (heightMapEnabled) {
            altitude = HeightMap.getHeight(position, 1e-6) / 1000 + distance;
        }
        //TODO System.err.println("Camera: updating, altitude=" + altitude);
        
        Matrix4 newCameraMatrix = geometry.getModelCameraTransformation(position, altitude);
        tiltTransformation(newCameraMatrix);

        Vector3 cameraPosition = newCameraMatrix.transform(new Vector3(0, 0, 0)).divide();
        Vector3 viewVector = newCameraMatrix.transform(new Vector3(0, 0, -1)).divide()
                .minus(cameraPosition);

        if (geometry.getSurfaceCoordinates(cameraPosition, viewVector) != null) {
            modelCameraMatrix = newCameraMatrix;
            modelViewMatrix = modelCameraMatrix.inverse();
            Matrix4 skyCameraMatrix = geometry.getSkyCameraTransformation(position, altitude);
            tiltTransformation(skyCameraMatrix);
            skyViewMatrix = skyCameraMatrix.inverse();
            updateCameraTransformation();
            return true;
        } else {
            return false;
        }
    }


    private class SurfaceHeightListener implements SurfaceListener {

        @Override
        public void surfaceChanged(double lonFrom, double latFrom, double lonTo, double latTo) {

            if (heightMapEnabled && position.getLatitude() >= latFrom 
                    && position.getLatitude() <= latTo && position.getLongitude() >= lonFrom 
                    && position.getLongitude() <= lonTo) {
                if (!updateCamera()) {
                    throw new IllegalStateException();
                }
            }
        }
    }


    /**
     * Constructor.
     * 
     * Creates a {@link de.joglearth.geometry.Camera} with FOV 90°, aspect ratio 1:1, zNear=0.1 and
     * zFar=1000.
     * 
     * @param geo The Geometry object
     */
    public Camera(Geometry geo) {
        if (geo == null) {
            throw new IllegalArgumentException();
        }

        geometry = geo;
        setPerspective((double) PI / 2, 1, 0.01, 100);
        if (!updateCamera()) {
            throw new IllegalStateException();
        }
        HeightMap.addSurfaceListener(new SurfaceHeightListener());
        
        updatesEnabled = true;
    }

    /**
     * Sets a new {@link de.joglearth.geometry.Geometry} object for model-specific computations.
     * 
     * @param g The new Geometry object. Must not be null.
     */
    public synchronized void setGeometry(Geometry g) {
        if (g == null) {
            throw new IllegalArgumentException();
        }

        this.geometry = g;

        if (!updateCamera()) {

            // a tilt that might be valid for a plane might not be valid for a sphere model!
            tiltX = 0;
            tiltY = 0;
            if (!updateCamera()) {
                throw new IllegalStateException();
            }
        }
    }
    
   
    public synchronized void setHeightMapEnabled(boolean enabled) {
        if (enabled != heightMapEnabled) {
            heightMapEnabled = enabled;
            if (!updateCamera()) {
                throw new IllegalStateException();
            }
        }
    }
    

    /**
     * Sets the position the {@link de.joglearth.geometry.Camera} is currently over.
     * 
     * @param coords The <code>GeoCoordinates</code> of the camera's position. Must not be null.
     */
    public synchronized void setPosition(GeoCoordinates coords) {
        if (coords == null) {
            throw new IllegalArgumentException();
        }

        GeoCoordinates oldPosition = position;
        this.position = coords;
        if (!updateCamera()) {
            position = oldPosition;
        }
    }

    /**
     * Returns the position the camera is currently over.
     * 
     * @return The position.
     */
    public GeoCoordinates getPosition() {
        return position;
    }

    /**
     * Sets the {@link de.joglearth.geometry.Camera}'s distance to the surface.
     * 
     * @param distance The distance. Must be positive and finite.
     */
    public synchronized void setDistance(double distance) {
        if (isInfinite(distance) || isNaN(distance) || distance <= 0) {
            throw new IllegalArgumentException();
        }

        double oldDistance = this.distance;
        this.distance = distance;
        if (!updateCamera()) {
            this.distance = oldDistance;
        }
    }

    /**
     * Returns the camera's distance to the surface.
     * 
     * @return The distance.
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Returns the camera's scale
     * 
     * @return
     */
    public double getScale() {
        return distance * tan(fov / 2) / PI;
    }

    /**
     * Sets the parameters for the perspective transformation done by the projection matrix.
     * 
     * @param fov The field of view, in radians. This is the angular distance of the left and right
     *        clipping plane. 90° (PI/2) by default
     * @param aspectRatio The aspect ratio, i.e. the width-to-height ratio. 1 by default
     * @param near The distance of the near clipping plane to the camera position. 0.1 by default
     * @param far The distance of the far clipping plane to the camera position. 1000.0 by default
     */
    public synchronized void setPerspective(double fov, double aspectRatio,
            double near, double far) {

        if (isNaN(fov) || fov <= 0 || fov > PI || isNaN(aspectRatio) || isInfinite(aspectRatio)
                || aspectRatio <= 0 || isNaN(near) || isInfinite(near) || isNaN(far)
                || isInfinite(far) || near >= far) {
            RuntimeException up = new IllegalArgumentException();
            throw up;
        }

        this.fov = fov;
        this.aspectRatio = aspectRatio;
        double f = 1 / tan(fov * 0.5);

        projectionMatrix = new Matrix4(new double[] {
                f / aspectRatio, 0, 0, 0,
                0, f, 0, 0,
                0, 0, (far + near) / (near - far), -1,
                0, 0, (2 * near * far) / (near - far), 0
        });
        
        updateCameraTransformation();
    }

    /**
     * Resets the {@link de.joglearth.geometry.Camera} tilt to x=y=0.
     */
    public synchronized void resetTilt() {
        tiltX = 0;
        tiltY = 0;

        if (!updateCamera()) {
            throw new IllegalStateException();
        }
    }

    /**
     * Sets the {@link de.joglearth.geometry.Camera} tilt to a specific value.
     * 
     * @param x The tilt around the x axis ("up and down"). Must be inside the interval [-pi/2,
     *        pi/2].
     * @param y The tilt around the y axis ("left and right"). Must be inside the interval [-pi/2,
     *        pi/2].
     */
    public synchronized void setTilt(double x, double y) {

        if (isNaN(x) || isNaN(y) || x < -PI / 2 || x > PI / 2 || y < -PI / 2 || y > PI / 2) {
            throw new IllegalArgumentException();
        }

        double oldX = tiltX;
        double oldY = tiltY;

        tiltX = x;
        tiltY = y;

        if (!updateCamera()) {
            tiltX = oldX;
            tiltY = oldY;
        }
    }

    /**
     * Changes the tilt by a difference value.
     * 
     * @param deltaX The tilt difference around the x axis ("up and down")
     * @param deltaY The tilt difference around the y axis ("left and right")
     */
    public synchronized void tilt(double deltaX, double deltaY) {

        if (isNaN(deltaX) || isNaN(deltaY) || isInfinite(deltaY) || isInfinite(deltaX)) {
            throw new IllegalArgumentException();
        }

        double oldX = tiltX;
        double oldY = tiltY;

        this.tiltX += deltaX;
        this.tiltY += deltaY;

        if (tiltX > PI / 2) {
            tiltX = PI / 2;
        } else if (tiltX < -PI / 2) {
            tiltX = -PI / 2;
        }

        if (tiltY > PI / 2) {
            tiltY = PI / 2;
        } else if (tiltY < -PI / 2) {
            tiltY = -PI / 2;
        }

        if (!updateCamera()) {
            tiltX = oldX;
            tiltY = oldY;
        }
    }

    /**
     * Changes the surface position of the {@link de.joglearth.geometry.Camera} by difference
     * values.
     * 
     * @param deltaLon The angular distance to move in longitude direction
     * @param deltaLat The angular distance to move in latitude direction
     */
    public void move(double deltaLon, double deltaLat) {
        setPosition(new GeoCoordinates(position.getLongitude() + deltaLon,
                position.getLatitude() + deltaLat));
    }

    private boolean isPointVisible(Vector3 point) {
        Vector3 t = transformationMatrix.transform(point).divide();
        return ((t.x >= -1 && t.x <= 1) && (t.y >= -1 && t.y <= 1) && (t.z >= -1 && t.z <= 1));
    }

    /**
     * Determines whether a surface point is visible by the {@link de.joglearth.geometry.Camera}.
     * The visibility is limited by both the viewport (the clipping planes) and parts of the scene
     * closer to the camera that might shadow others (the back of the globe, for example).
     * 
     * @param geo The coordinates to check for
     * @return Whether the point is visible
     */
    public boolean isPointVisible(GeoCoordinates geo) {
        if (geo == null) {
            throw new IllegalArgumentException();
        }

        Vector3 cameraPosition = modelCameraMatrix.transform(new Vector3(0, 0, 0)).divide();

        return isPointVisible(geometry.getSpacePosition(geo))
                && geometry.isPointVisible(cameraPosition, geo);
    }

    /**
     * Calculates the screen coordinates of a visible point given in longitude and latitude
     * coordinates. If the point is not visible, the result is unspecified.
     * 
     * @param geo The point's coordinates
     * @return The coordinates of the point on the screen
     */
    public synchronized ScreenCoordinates getScreenCoordinates(GeoCoordinates geo) {
        if (geo == null) {
            throw new IllegalArgumentException();
        }

        Vector3 t = transformationMatrix.transform(geometry.getSpacePosition(geo)).divide();

        if ((t.x >= -1 && t.x <= 1) && (t.y >= -1 && t.y <= 1)) {
            return new ScreenCoordinates((t.x + 1) / 2, (t.y + 1) / 2);
        } else {
            return null;
        }
    }

    /**
     * Calculates the longitude and latitude coordinates of the point underneath a point on the
     * screen.
     * 
     * @param screen The screen coordinates
     * @return The surface coordinates if the point maps to the surface, <code>null</code> if it
     *         points outside the plane or globe ("space")
     */
    public synchronized GeoCoordinates getGeoCoordinates(ScreenCoordinates screen) {
        if (screen == null) {
            throw new IllegalArgumentException();
        }

        if (screen.x < 0 || screen.x > 1 || screen.y < 0 || screen.y > 1) {
            return null;
        }

        Vector3 cameraPosition = modelCameraMatrix.transform(new Vector3(0, 0, 0)).divide();
        Vector3 earthAxis = modelCameraMatrix.transform(new Vector3(0, 1, 0)).divide()
                .minus(cameraPosition);
        
        Vector3 zAxis = modelCameraMatrix.transform(new Vector3(0, 0, 1)).divide()
                .minus(cameraPosition).normalized();
        Vector3 xAxis = earthAxis.crossProduct(zAxis).normalized();
        Vector3 yAxis = zAxis.crossProduct(xAxis).normalized();
        
        double yAngle = asin((1-2*screen.x)*sin(fov/2*aspectRatio));        
        double xAngle = asin((1-2*screen.y)*sin(fov/2));

        Matrix4 directionMatrix = modelCameraMatrix.clone();
        directionMatrix.rotate(yAxis, yAngle);
        directionMatrix.rotate(xAxis, xAngle);

        Vector3 viewVector = directionMatrix.transform(new Vector3(0, 0, -1)).divide()
                .minus(cameraPosition);

        System.err.format("Camera.getGeoCoordinates(): Screen Position: %s, Camera Position: %s,"
                + " Camera Axes: X=%s, Y=%s, Z=%s, Direction angles: Y-Rotation=%.3f°, X-Rotation="
                + "%.3f°, View Vector: %s, Model intersection at: %s\n", screen, cameraPosition, 
                xAxis, yAxis, zAxis, yAngle * 180 / PI, xAngle * 180 / PI, viewVector, 
                geometry.getSurfaceCoordinates(cameraPosition, viewVector));
        return geometry.getSurfaceCoordinates(cameraPosition, viewVector);
    }

    /**
     * Returns the projection matrix derived from the current camera settings.
     * 
     * The result may be directly passed to OpenGL as a projection matrix, and is guaranteed to
     * produce a picture corresponding to whatever results the camera visibility and projection
     * methods produced for these settings.
     * 
     * @return The projection matrix
     */
    public Matrix4 getProjectionMatrix() {
        return projectionMatrix;
    }
    
    public Matrix4 getModelViewMatrix() {
        return modelViewMatrix;
    }
    
    public Matrix4 getSkyViewMatrix() {
        return skyViewMatrix;
    }
    
    public Matrix4 getTransformationMatrix() {
        return transformationMatrix;
    }

    /**
     * Adds a new {@link de.joglearth.geometry.CameraListener}.
     * 
     * @param l The new <code>CameraListener</code>
     */
    public void addCameraListener(CameraListener l) {
        if (l == null) {
            throw new IllegalArgumentException();
        }

        listeners.add(l);
    }

    /**
     * Removes a given {@link de.joglearth.geometry.CameraListener}.
     * 
     * @param l The <code>CameraListener</code> that should be removed
     */
    public void removeCameraListener(CameraListener l) {
        if (l == null) {
            throw new IllegalArgumentException();
        }

        listeners.remove(l);
    }
}
