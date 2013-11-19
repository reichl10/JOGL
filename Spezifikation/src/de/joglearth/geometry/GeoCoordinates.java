package de.joglearth.geometry;

public class GeoCoordinates implements Cloneable {
	public float longitude;
	public float latitude;
	
	public GeoCoordinates(float lon, float lat) {
		longitude = lon;
		latitude = lat;
	}
	
	public GeoCoordinates clone() {
		return new GeoCoordinates(longitude, latitude);
	}
}
