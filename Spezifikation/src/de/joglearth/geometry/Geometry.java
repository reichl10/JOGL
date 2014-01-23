package de.joglearth.geometry;

import de.joglearth.geometry.Matrix4;
import de.joglearth.geometry.Vector3;


/**
 * Abstracts geometric calculations dependent on the map model (plane or sphere).
 */
public interface Geometry {

    /**
     * Returns whether a point, given by longitude and latitude coordinates, could be visible
     * provided that the field of view and distance are large enough.
     * 
     * @param geo The surface point
     * @return Whether the point might be visible
     */
    public boolean isPointVisible(Vector3 cameraPosition, GeoCoordinates geo);

    /**
     * Determines the three-dimensional position of a surface point in model space. The height map
     * is ignored.
     * 
     * @param geo The surface point
     * @return The position
     */
    public Vector3 getSpacePosition(GeoCoordinates geo, double altitude);

    /**
     * Calculates the surface coordinates of the intersection from a straight line between a given
     * point and the model center (The globes center or infinity for the map plane) and the map
     * surface.
     * 
     * @param cameraPosition The actual position of the camera
     * @param viewVector The origin point
     * @return The surface coordinates
     */
    public GeoCoordinates getSurfaceCoordinates(Vector3 cameraPosition, Vector3 viewVector);

    /**
     * Constructs the matrix transforming the default GL camera with position (0, 0, 0), direction
     * (0, 0, -1) and upwards pointing vector (0, 1, 0) into the camera perspective for rendering
     * the globe / plane model.
     * 
     * @return The camera transformation
     */
    public Matrix4 getModelCameraTransformation(GeoCoordinates position, double altitude);

    /**
     * Constructs the matrix transforming the default GL camera with position (0, 0, 0), direction
     * (0, 0, -1) and upwards pointing vector (0, 1, 0) into the camera perspective for rendering
     * the background. This usually differs from {@link getModelCameraTransformation} in that it
     * omits all translations while keeping rotations incurred by the camera's position over the
     * surface.
     * 
     * @return The camera transformation
     */
    public Matrix4 getSkyCameraTransformation(GeoCoordinates position, double altitude);

    /**
     * Determines the surface distortion factor in longitudinal direction at a given position.
     * 
     * @param position the position
     * @return The surface distortion where 1 means no distortion
     */
    public double getLongitudeDistortion(GeoCoordinates position);

    /**
     * Whether the underlying geometric model allows traversal of the 180° line of longitude. This
     * is e.g. true for a sphere, butt false for a plane.
     *  
     * @return true or false. duh
     */
    public boolean allowsLongitudinalTraversal();

}
