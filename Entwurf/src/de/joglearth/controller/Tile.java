package de.joglearth.controller;


// Speichert eine Kachel über ihre Längen- und Breitengradgrenzen.
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