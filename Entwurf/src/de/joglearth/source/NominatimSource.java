package de.joglearth.source;

import de.joglearth.location.Location;
import de.joglearth.rendering.*;
import de.joglearth.source.*;
import de.joglearth.ui.*;

public class NominatimSource extends Source<NominatimQuery, Location[]> {

	public NominatimSource(RequestListener<NominatimQuery, Location[]> owner) {
		super(owner);
	}

	@Override
	public Location[] requestObject(NominatimQuery k) {
		return null;
	}
}