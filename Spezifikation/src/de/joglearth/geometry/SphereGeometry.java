package de.joglearth.geometry;

import de.joglearth.geometry.Matrix4;
import de.joglearth.geometry.Vector3;


/**
 * Implements the {@link de.joglearth.geometry.Geometry} interface for a camera looking at a globe.
 */
public class SphereGeometry implements Geometry {

    @Override
    public boolean isPointVisible(Vector3 cameraPosition, GeoCoordinates geo) {
        return cameraPosition.to(getSpacePosition(geo)).length() <= cameraPosition.length();
    }

    @Override
    public Vector3 getSpacePosition(GeoCoordinates geo) {
        return new Vector3(Math.sin(geo.getLongitude()),
                Math.sin(geo.getLatitude()),
                Math.cos(geo.getLongitude()));
    }

    @Override
    public GeoCoordinates getSurfaceCoordinates(Vector3 cameraPosition, Vector3 viewVector) {
        Vector3 c = cameraPosition, v = viewVector;
        double denom = 1 / (v.x * v.x + v.y * v.y + v.z * v.z);
        double sqrt_arg = Math.pow(c.x * v.x + c.y * v.y + c.z * v.z, 2)
                - (c.x * c.x + c.y * c.y + c.z * c.z - 1) * (v.x * v.x + v.y * v.y + v.z * v.z);
        
        if (denom == 0 || sqrt_arg < 0) {
            return null;
        }
        
        double lambda = (-c.x*v.x - c.y*v.y - c.z*v.z - Math.sqrt(sqrt_arg))/denom;        
        double cos_lon = c.x + lambda*v.x;
        double sin_lat = c.y + lambda*v.y;
        
        if (cos_lon < -1 || cos_lon > 1 || sin_lat < -1 || sin_lat > 1) {
            return null;
        }
        
        return new GeoCoordinates(Math.acos(cos_lon), Math.asin(sin_lat));
    }

    @Override
    public Matrix4 getViewMatrix(GeoCoordinates position, double distance) {
        return null;
    }

}
