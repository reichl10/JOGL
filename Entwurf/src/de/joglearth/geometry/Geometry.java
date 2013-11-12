package de.joglearth.geometry;

import de.joglearth.geometry.Matrix4;
import de.joglearth.geometry.Vector3;

public interface Geometry {
	
	public boolean isPointVisible(float longitude, float latitude);
	
	
	// Gibt Point zurück, falls Punkt sichtbar, sonst null.
	// x- und y-Koordinaten des Points sind zwischen 0 und 1, was die 
	// Bildschirmposition festlegt.
	public Vector3 getSpacePosition(float longitude, float latitude);
	
	
	// Die koordinaten screen{X,Y} sind zwischen 0 und 1.
	// Gibt Längen- und Breitengrad zurück, falls unter dem Punkt
	// die Kugel/Ebene liegt, sonst null.
	public Point getSurfaceCoordinates(Vector3 viewVector);
	
	
	// Berechnet die Darstellungsmatrix aus den Attributen
	public Matrix4 getViewMatrix();
	
}
