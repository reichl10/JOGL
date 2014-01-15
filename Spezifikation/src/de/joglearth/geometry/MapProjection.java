package de.joglearth.geometry;

public interface MapProjection {

	double projectLongitude(double longitude);
	
	double projectLatitude(double latitude);
}
