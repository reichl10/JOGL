package de.joglearth.geometry;

import de.joglearth.geometry.Matrix4;
import de.joglearth.geometry.Vector3;


/**
 * Implements the {@link de.joglearth.geometry.Geometry} interface for a camera looking at a map
 * plane.
 */
public class PlaneGeometry implements Geometry {

    private static final double DISTANCE_LIMIT = 10;


    @Override
    public boolean isPointVisible(Vector3 cameraPosition, GeoCoordinates geo) {
        Vector3 surfacePosition = cameraPosition.clone();
        surfacePosition.z = 0;
        return surfacePosition.to(getSpacePosition(geo)).length() <= DISTANCE_LIMIT;
    }

    @Override
    public Vector3 getSpacePosition(GeoCoordinates geo) {
        return new Vector3(geo.getLongitude() / Math.PI,
                           geo.getLatitude() / Math.PI,
                           0);
    }

    @Override
    public GeoCoordinates getSurfaceCoordinates(Vector3 cameraPosition, Vector3 viewVector) {
        if (viewVector.z >= 0 || cameraPosition.z <= 0 || cameraPosition.equals(viewVector)) {
            return null;
        }
        
        double lon = (cameraPosition.x-cameraPosition.z/viewVector.z*viewVector.x) * Math.PI;
        double lat = (cameraPosition.y-cameraPosition.z/viewVector.z*viewVector.y) * Math.PI;
        
        if (lon > -Math.PI && lon <= Math.PI && lat >= -Math.PI/2 && lat <= Math.PI/2) {
            return new GeoCoordinates(lon, lat);
        } else {
            return null;
        }
    }

    @Override
    public Matrix4 getViewMatrix(GeoCoordinates position, double altitude) {
        Matrix4 mat = new Matrix4();
        mat.translate(position.getLongitude()/Math.PI, position.getLatitude()/Math.PI, altitude);
        return mat;
    }

}
