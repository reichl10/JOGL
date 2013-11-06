package de.joglearth.controller;

import de.joglearth.model.Point;
import de.joglearth.model.Tile;
import de.joglearth.model.UpdateListener;

/*
 * getDisplayedArea() zwecks overpass & nominatim & renderer(abfrage derzeitiger ausschnitt)
 */
public class Camera {
	
	private float longitude;
	private float latitude;
	private float distance;
	private float tiltX;
	private float tiltY;
	private float aspectRatio;
	private float fov;
	private Type type;
	
	public enum Type {
		PLANE,
		SPHERE
	}
	
	public Camera(Type t) {
		
	}
	
	public void setType (Type t) {
		this.type = t;
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
	public Point getWindowPosition(float longitude, float latitude) {
		return null;
	}
	
	
	// Die koordinaten screen{X,Y} sind zwischen 0 und 1.
	// Gibt Längen- und Breitengrad zurück, falls unter dem Punkt
	// die Kugel/Ebene liegt, sonst null.
	public Point getCoordinates(float screenX, float screenY) {
		return null;
	}
	
	// Gibt die sichtbaren Kacheln zurück.
	// Kachelgrößen und Position können von der Kamera aus dem sichtbaren 
	// Kugelteil bestimmt werden.
	public Tile[] getVisibleTiles() {
		return null;
	}
	
	// Berechnet die Darstellungsmatrix aus den Attributen
	public Matrix4 getProjectionMatrix() {
		return null;
	}
	
}
