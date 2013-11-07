package de.joglearth.model;

import de.joglearth.model.*;
import de.joglearth.view.*;
import de.joglearth.controller.*;

public class NominatimSource extends Source<NominatimQuery, Location[]> {

	public NominatimSource(RequestListener<NominatimQuery, Location[]> owner) {
		super(owner);
	}

	@Override
	public Location[] requestObject(NominatimQuery k) {
		return null;
	}
}