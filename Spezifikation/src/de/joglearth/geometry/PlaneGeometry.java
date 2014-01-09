package de.joglearth.geometry;

import static java.lang.Math.*;
import de.joglearth.geometry.Matrix4;
import de.joglearth.geometry.Vector3;
import de.joglearth.height.HeightMap;


/**
 * Implements the {@link de.joglearth.geometry.Geometry} interface for a camera looking at a map
 * plane.
 */
public class PlaneGeometry implements Geometry {

    private static final double DISTANCE_LIMIT = 10;


    @Override
    public boolean isPointVisible(Vector3 cameraPosition, GeoCoordinates geo) {
        if (cameraPosition == null || geo == null) {
            throw new IllegalArgumentException();
        }

        /*
         * A point is visible if the intersection of the camera position's perpendicular with the
         * plane ("ground position") is not farther away from it than the distance of camera and
         * plane times the DISTANCE_LIMIT.
         */

        if (cameraPosition.z <= 0) {
            return false;
        }

        Vector3 surfacePosition = cameraPosition.clone();
        surfacePosition.z = 0;
        return surfacePosition.to(getSpacePosition(geo)).length()
                                <= cameraPosition.z * DISTANCE_LIMIT;
    }

    @Override
    public Vector3 getSpacePosition(GeoCoordinates geo) {
        /*
         * The plane is laid out to be 2 units wide, 1 unit high and perpendicular to the Z axis,
         * with lon=0/lat=0 being the center (0, 0, 0).
         */
        return new Vector3(geo.getLongitude(), geo.getLatitude(), 0);
    }

    @Override
    public GeoCoordinates getSurfaceCoordinates(Vector3 cameraPosition, Vector3 viewVector) {
        if (cameraPosition == null || viewVector == null) {
            throw new IllegalArgumentException();
        }

        /*
         * If the viewer looks in positive Z direction, the camera has a negative Z coordinate, or
         * the view vector is the zero vector, he is not looking down on the plane. Therefore, there
         * is no intersection.
         */
        if (viewVector.z >= 0 || cameraPosition.z <= 0 || viewVector.length() == 0) {
            return null;
        }

        double lon = (cameraPosition.x - cameraPosition.z / viewVector.z * viewVector.x);
        double lat = (cameraPosition.y - cameraPosition.z / viewVector.z * viewVector.y);

        // These conditions hold if and only if the centered point is on the plane.
        if (lon > -PI && lon <= PI && lat >= -PI / 2 && lat <= PI / 2) {
            return new GeoCoordinates(lon, lat);
        } else {
            return null;
        }
    }

    @Override
    public Matrix4 getModelCameraTransformation(GeoCoordinates position, double altitude) {
        if (position == null || altitude <= HeightMap.MIN_HEIGHT || Double.isInfinite(altitude)
                || Double.isNaN(altitude)) {
            throw new IllegalArgumentException();
        }

        /*
         * Assumes that the default looking direction is (0, 0, -1). Might have to be corrected by
         * adding a rotate()ion.
         */
        Matrix4 mat = new Matrix4();
        mat.translate(position.getLongitude(), position.getLatitude(), altitude 
                - HeightMap.MIN_HEIGHT);
        return mat;
    }

    @Override
    public Matrix4 getSkyCameraTransformation(GeoCoordinates position, double altitude) {
        if (position == null || altitude <= 0 || Double.isInfinite(altitude)
                || Double.isNaN(altitude)) {
            throw new IllegalArgumentException();
        }
        
        return new Matrix4();
    }
}
