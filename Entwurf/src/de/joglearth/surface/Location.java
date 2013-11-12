package de.joglearth.surface;

import de.joglearth.geometry.GeoCoordinates;


public class Location implements Cloneable {
	public GeoCoordinates point;
	public LocationType type;
	public String details;

	public Location(GeoCoordinates point, LocationType type, String details) {
		this.point = point;
		this.details = details;
	}
}
