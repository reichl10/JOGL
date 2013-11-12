package de.joglearth.surface;

import de.joglearth.geometry.Point;
import de.joglearth.rendering.*;
import de.joglearth.source.*;
import de.joglearth.ui.*;

public class Location {
	private Point point;
	private LocationType type;
	private String details;
	
	public Location(Point point, LocationType type, String details) {
		this.point = point;
		this.details = details;
	}
}
