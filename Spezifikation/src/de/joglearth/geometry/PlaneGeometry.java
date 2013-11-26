package de.joglearth.geometry;

import de.joglearth.geometry.Matrix4;
import de.joglearth.geometry.Vector3;

/**
 * Implements the {@link Geometry} interface for a camera looking at a map plane.
 */
public class PlaneGeometry implements Geometry {

	@Override
	public boolean isPointVisible(GeoCoordinates geo) {
		return false;
	}

	@Override
	public Vector3 getSpacePosition(GeoCoordinates geo) {
		return null;
	}

	@Override
	public ScreenCoordinates getSurfaceCoordinates(Vector3 viewVector) {
		return null;
	}

	@Override
	public Matrix4 getViewMatrix() {
		return null;
	}
	
}