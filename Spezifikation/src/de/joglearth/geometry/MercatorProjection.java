package de.joglearth.geometry;

import static java.lang.Math.*;

public class MercatorProjection implements MapProjection {

	@Override
	public double projectLongitude(double longitude) {
		return longitude;
	}

	@Override
	public double projectLatitude(double latitude) {
		return log(tan(latitude) + 1/cos(latitude));
	}
	
	@Override
	public int hashCode() {
		return "MercatorProjection".hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && this.getClass() == other.getClass();
	}

}
