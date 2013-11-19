package de.joglearth.source;

import de.joglearth.geometry.ScreenCoordinates;
import de.joglearth.geometry.Tile;


public class NominatimQuery {
	public enum Type {
		GLOBAL,
		LOCAL,
		POINT
	}
	
	public Type type;
	public Tile area;
	public ScreenCoordinates point;
	public String query;
}
