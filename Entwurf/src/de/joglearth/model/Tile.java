package de.joglearth.model;


// Speichert eine Kachel über ihre Längen- und Breitengradgrenzen.
public class Tile {
	
	public float longFrom, longTo, latFrom, latTo;
	
	public Tile(float longFrom, float longTo, float latFrom, float latTo) {
		this.longFrom = longFrom;
		this.longTo = longTo;
		this.latFrom = latFrom;
		this.latTo = latTo;
	}
	
}
