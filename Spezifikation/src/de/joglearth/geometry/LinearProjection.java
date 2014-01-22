package de.joglearth.geometry;

/**
 * Defines a linear projection for map types that don't require a projection, as the values
 * are already valid.
 */
public class LinearProjection implements MapProjection {

	@Override
	public double projectLongitude(double longitude) {
		return longitude;
	}

	@Override
	public double projectLatitude(double latitude) {
		return latitude;
	}
	
	@Override
	public int hashCode() {
		return "LinearProjection".hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && this.getClass() == other.getClass();
	}

}
