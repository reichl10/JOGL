package de.joglearth.geometry;

public class ScreenCoordinates implements Cloneable {
	public float x;
	public float y;
	
	public ScreenCoordinates(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public ScreenCoordinates clone() {
		return new ScreenCoordinates(x, y);
	}
}
