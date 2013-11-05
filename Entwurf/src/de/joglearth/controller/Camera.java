package de.joglearth.controller;

import de.joglearth.model.Point;
import de.joglearth.model.Tile;

public abstract class Camera {
	
	protected float longitude;
	protected float latitude;
	protected float distance;
	protected float tiltX;
	protected float tiltY;
	protected float aspectRatio;
	protected float fov;
	
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
	
	public void move(float longitude, float latitude) {
		this.longitude += longitude;
		this.latitude += latitude;		
	}
	
	public boolean isPointVisible(float longitude, float latitude) {
		return false;
	}
	
	
	// Gibt Point zurück, falls Punkt sichtbar, sonst null.
	// x- und y-Koordinaten des Points sind zwischen 0 und 1, was die 
	// Bildschirmposition festlegt.
	public abstract Point getWindowPosition(float longitude, float latitude);
	
	
	// Die koordinaten screen{X,Y} sind zwischen 0 und 1.
	// Gibt Längen- und Breitengrad zurück, falls unter dem Punkt
	// die Kugel/Ebene liegt, sonst null.
	public abstract Point getCoordinates(float screenX, float screenY);
	
	// Gibt die sichtbaren Kacheln zurück.
	// Kachelgrößen und Position können von der Kamera aus dem sichtbaren 
	// Kugelteil bestimmt werden.
	public abstract Tile[] getVisibleTiles();
	
	// Berechnet die Darstellungsmatrix aus den Attributen
	public abstract Matrix4 getProjectionMatrix();
	
}
