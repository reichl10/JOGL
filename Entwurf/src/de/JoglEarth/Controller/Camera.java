package de.JoglEarth.Controller;

import de.JoglEarth.Model.Point;

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
	
	public Point getWindowPosition() {
		return null;
	}
}