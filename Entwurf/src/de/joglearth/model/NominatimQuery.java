package de.joglearth.model;

public class NominatimQuery {
	public enum Type {
		GLOBAL,
		LOCAL,
		POINT
	}
	
	public Type type;
	public Tile area;
	public Point point;
	public String query;
}
