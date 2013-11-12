package de.joglearth.geometry;

import de.joglearth.geometry.Matrix4;
import de.joglearth.geometry.Vector3;

public class PlaneGeometry implements Geometry {

	@Override
	public boolean isPointVisible(float longitude, float latitude) {
		// TODO Automatisch erstellter Methoden-Stub
		return false;
	}

	@Override
	public Vector3 getSpacePosition(float longitude, float latitude) {
		// TODO Automatisch erstellter Methoden-Stub
		return null;
	}

	@Override
	public ScreenCoordinates getSurfaceCoordinates(Vector3 viewVector) {
		// TODO Automatisch erstellter Methoden-Stub
		return null;
	}

	@Override
	public Matrix4 getViewMatrix() {
		// TODO Automatisch erstellter Methoden-Stub
		return null;
	}
	
}