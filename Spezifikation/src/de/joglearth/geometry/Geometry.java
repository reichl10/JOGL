package de.joglearth.geometry;

import de.joglearth.geometry.Matrix4;
import de.joglearth.geometry.Vector3;


/**
 * Abstracts geometric calculations dependent on the map model (plane or sphere).
 */
public interface Geometry {

    /**
     * Returns whether a point, given by longitude and latitude coordinates, could be visible given
     * that the field of view and distance is large enough.
     * 
     * @param geo The surface point.
     * @return Whether the point might be visible.
     */
    public boolean isPointVisible(GeoCoordinates geo);

    /**
     * Determines the three-dimensional position of a surface point in model space. The height map
     * is ignored.
     * 
     * @param geo The surface point.
     * @return The position.
     */
    public Vector3 getSpacePosition(GeoCoordinates geo);

    /**
     * Calculates the surface coordinates of the intersection from a straight line between a given
     * point and the model center (The globes center or infinity for the map plane) and the map
     * surface.
     * 
     * @param viewVector The origin point.
     * @return The surface coordinates.
     */
    public ScreenCoordinates getSurfaceCoordinates(Vector3 cameraPosition, Vector3 viewVector);

    /**
     * Returns the view matrix performing translations and rotations incurred by the camera
     * position.
     * 
     * @return The view matrix.
     */
    public Matrix4 getViewMatrix();

}
