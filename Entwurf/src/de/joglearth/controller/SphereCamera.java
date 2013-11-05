package de.joglearth.controller;

import de.joglearth.model.Point;
import de.joglearth.model.Tile;

public class SphereCamera extends Camera {

	@Override
	public Point getWindowPosition(float longitude, float latitude) {
		return null;
	}

	@Override
	public Point getCoordinates(float screenX, float screenY) {
		return null;
	}

	@Override
	public Tile[] getVisibleTiles() {
		return null;
	}

	@Override
	public Matrix4 getProjectionMatrix() {
		return null;
	}

}
