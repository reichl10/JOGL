package de.joglearth.controller;

import de.joglearth.model.*;
import de.joglearth.view.*;
import de.joglearth.controller.*;

public class Location {
	private Point point;
	private PoiType type;
	private String details;
	
	public Location(Point point, PoiType type, String details) {
		this.point = point;
		this.type = type;
		this.details = details;
	}
}
