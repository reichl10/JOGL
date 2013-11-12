package de.joglearth.geometry;

import de.joglearth.geometry.Matrix4;
import de.joglearth.geometry.Vector3;

/*
 * getDisplayedArea() zwecks overpass & nominatim & renderer(abfrage derzeitiger ausschnitt)
 */
public class Camera {
	
	private float longitude;
	private float latitude;
	private float distance;
	private float tiltX;
	private float tiltY;
	private Matrix4 clipMatrix, projectionMatrix;
	private Geometry geometry;
	
	private void updateProjectionMatrix() {
		Matrix4 cameraMatrix = new Matrix4();
		cameraMatrix.rotateX(tiltX);
		cameraMatrix.rotateY(tiltY);
		projectionMatrix = cameraMatrix.inverse();
		projectionMatrix.mult(clipMatrix);
	}
	
	public Camera() {
		setPerspective((float) Math.PI/2, 1, 0.1f, 1000);
	}
	
	public void setGeometry(Geometry g) {
		this.geometry = g;
	}
	
	public void setPosition(float longitude, float latitude) {
		this.longitude = longitude;
		this.latitude = latitude;
		updateProjectionMatrix();
	}
	
	public void setDistance(float distance) {
		this.distance = distance;
		updateProjectionMatrix();
	}
		
	public void setPerspective(float fov, float aspectRatio, float near,
			float far) {
		float f = 1.f / (float) Math.tan(fov*0.5f);
		float[] d = { f * aspectRatio,   0,			          0,  0, 
				      0,               f, 	            	      0,  0,
				      0,                 0,       (far+near)/(far-near),  1,
				      0,                 0, (2.f*near*far) / (near-far),  0 };
		clipMatrix = new Matrix4(d);
	}
	
	public void resetTilt() {
		tiltX = 0;
		tiltY = 0;
		updateProjectionMatrix();
	}
	
	public void tilt(float x, float y) {
		this.tiltX += x;
		this.tiltY += y;
		updateProjectionMatrix();
	}
	
	public void move(float longitude, float latitude) {
		this.longitude += longitude;
		this.latitude += latitude;		
		updateProjectionMatrix();
	}
	
	
	private boolean isPointVisisble(Vector3 point) {
		// Sichtbar, wenn: Transformierter Vektor in [0, 1] x [0, 1] x [0, inf]
		// und, falls Kugel, z <= Abstand zu (0, 0, 0) [?!]
		return false;
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
		return projectionMatrix;
	}
	
}

