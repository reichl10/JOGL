package de.joglearth.location;

import de.joglearth.geometry.Point;
import de.joglearth.rendering.*;
import de.joglearth.source.*;
import de.joglearth.ui.*;

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
