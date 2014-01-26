package de.joglearth.geometry;

import de.joglearth.geometry.Matrix4;
import de.joglearth.geometry.Vector3;
import static java.lang.Math.*;


/**
 * Implements the {@link de.joglearth.geometry.Geometry} interface for a camera looking at a globe.
 */
public class SphereGeometry implements Geometry {

    @Override
    public boolean isPointVisible(Vector3 cameraPosition, GeoCoordinates geo) {
        
        /* A point is on the back side of the sphere if its distance to the camera is greater
         * than the distance to the boundary point of the sphere and its tangent through the 
         * camera origin.
         */
        double cameraRadius = cameraPosition.length(),
               horizon = sqrt(cameraRadius*cameraRadius - 1);
        return getSpacePosition(geo, 0).to(cameraPosition).length() <= horizon;
    }

    @Override
    public Vector3 getSpacePosition(GeoCoordinates geo, double altitude) {
        // The earth axis is equal to the y axis, lon=0, lat=0 has the coordinates (0, 0, 1).
        return new Vector3(cos(geo.latitude) * sin(geo.longitude),
                sin(geo.latitude),
                cos(geo.latitude) * cos(geo.longitude)).times(1 + altitude);
    }

    @Override
    // TODO Check if lambda needs to be replaced with -lambda
    public GeoCoordinates getSurfaceCoordinates(Vector3 cameraPosition, Vector3 viewVector) {
        Vector3 c = cameraPosition, v = viewVector;
        
        /* The straight given by cameraPosition and viewVector is defined as x = c + lambda*v.
         * To find the (nearest) intersection point, the spherical equation x^2 + y^2 + z^2 is 
         * set equal to the linear equation and solved for lambda.
         * If no real solution exists, the line does not intersect the sphere.
         */
        double denom = 1 / (v.x * v.x + v.y * v.y + v.z * v.z);
        double sqrt_arg = pow(c.x * v.x + c.y * v.y + c.z * v.z, 2)
                - (c.x * c.x + c.y * c.y + c.z * c.z - 1) * (v.x * v.x + v.y * v.y + v.z * v.z);

        if (denom == 0 || sqrt_arg < 0) {
            return null;
        }

        double lambda = (-c.x * v.x - c.y * v.y - c.z * v.z - sqrt(sqrt_arg)) / denom;
                        
        double sin_lat = c.y + lambda * v.y;

        if (sin_lat < -1 || sin_lat > 1) {
            return null;
        }

        double lat = asin(sin_lat);
        double cos_lon = (c.z + lambda * v.z) / (cos(lat));

        if (cos_lon < -1 || cos_lon > 1) {
            return null;
        }

        double lon = acos(cos_lon);

        if (c.x + lambda * v.x < 0) {
            lon = -lon;
        }

        return new GeoCoordinates(lon, lat);
    }

    @Override
    public Matrix4 getModelCameraTransformation(GeoCoordinates position, double altitude) {
        if (position == null || altitude <= 0 || Double.isInfinite(altitude)
                || Double.isNaN(altitude)) {
            throw new IllegalArgumentException();
        }
                
        return getSkyCameraTransformation(position, altitude)
                .translate(0, 0, 1 + altitude);
    }
    

    @Override
    public Matrix4 getSkyCameraTransformation(GeoCoordinates position, double altitude) {
        if (position == null || altitude <= 0 || Double.isInfinite(altitude)
                || Double.isNaN(altitude)) {
            throw new IllegalArgumentException();
        }
                
        Vector3 earthAxis = new Vector3(0, 1, 0);        
        
        Matrix4 mat = Matrix4.IDENTITY
                .rotate(earthAxis, position.longitude)
                .translate(0, 0, 1);
        
        Vector3 cameraPosition = mat.transform(new Vector3(0, 0, 0)).divide(),
                viewVector = mat.transform(new Vector3(0, 0, -1)).divide().minus(cameraPosition),
                cameraXAxis = earthAxis.crossProduct(viewVector).normalized();
        
        return Matrix4.IDENTITY
            .rotate(cameraXAxis, position.latitude)
            .rotate(earthAxis, position.longitude);
    }

    @Override
    public double getLongitudeDistortion(GeoCoordinates position) {
        return cos(position.latitude);
    }

    @Override
    public boolean allowsLongitudinalTraversal() {
        return true;
    }
}
