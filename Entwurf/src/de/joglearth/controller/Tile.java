package de.joglearth.controller;

import de.joglearth.model.*;
import de.joglearth.view.*;
import de.joglearth.controller.*;


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