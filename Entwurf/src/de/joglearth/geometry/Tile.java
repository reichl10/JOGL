package de.joglearth.geometry;

import de.joglearth.rendering.*;
import de.joglearth.source.*;
import de.joglearth.ui.*;


// Speichert eine Kachel �ber ihre L�ngen- und Breitengradgrenzen.
public class Tile {
	
	public float longFrom, longTo, latFrom, latTo;
	public int zoomLevel;
	
	public Tile(float longFrom, float longTo, float latFrom, float latTo,
			int zoomLevel) {
		this.longFrom = longFrom;
		this.longTo = longTo;
		this.latFrom = latFrom;
		this.latTo = latTo;
		this.zoomLevel = zoomLevel;
	}
}