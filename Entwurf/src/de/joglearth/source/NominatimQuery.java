package de.joglearth.source;

import de.joglearth.geometry.Point;
import de.joglearth.geometry.Tile;
import de.joglearth.rendering.*;
import de.joglearth.source.*;
import de.joglearth.ui.*;


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
