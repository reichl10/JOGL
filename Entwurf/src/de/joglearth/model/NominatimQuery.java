package de.joglearth.model;

import de.joglearth.model.*;
import de.joglearth.view.*;
import de.joglearth.controller.*;


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
