package de.joglearth.model;

public class NominatimSource extends Source<NominatimQuery, Location[]> {

	public NominatimSource(RequestListener<NominatimQuery, Location[]> owner) {
		super(owner);
	}

	@Override
	public Location[] requestObject(NominatimQuery k) {
		return null;
	}
}