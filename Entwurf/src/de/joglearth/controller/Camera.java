package de.joglearth.controller;

import de.joglearth.model.Point;
import de.joglearth.model.Tile;

public class Camera {
	
	private float longitude;
	private float latitude;
	private float distance;
	private float tiltX;
	private float tiltY;
	private float aspectRatio;
	private float fov;
	
	public Camera(){
	}
	
	public void setPosition(float longitude, float latitude) {
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	public void setDistance(float distance) {
		this.distance = distance;
	}
	
	public void setFOV(float fov) {
		this.fov = fov;
	}
	
	public void setAspectRatio(float aspectRatio) {
		this.aspectRatio = aspectRatio;
	}
	
	public void resetTilt() {
		tiltX = 0;
		tiltY = 0;
	}
	
	public void tilt(float x, float y) {
		this.tiltX += x;
		this.tiltY += y;
	}
	
	public void rotate(float longitude, float latitude) {
		this.longitude += longitude;
		this.latitude += latitude;		
	}
	
	public boolean isPointVisible(float longitude, float latitude) {
		return false;
	}
	
	// Gibt Point zurück, falls Punkt sichtbar, sonst null 
	public Point getWindowPosition(float longitude, float latitude) {
		return null;
	}
	
	public Point getCoordinates(float screenX, float screenY) {
		return null;
	}
	
	public Tile[] getVisibleTiles() {
		return null;
	}
	
}
