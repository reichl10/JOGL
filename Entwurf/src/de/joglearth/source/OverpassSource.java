package de.joglearth.source;

import de.joglearth.location.Location;
import de.joglearth.rendering.*;
import de.joglearth.source.*;
import de.joglearth.ui.*;

public class OverpassSource extends Source<OverpassQuery, Location[]> {
	public OverpassSource(RequestListener<OverpassQuery, Location[]> owner) {
		super(owner);
	}

	@Override
	public Location[] requestObject(OverpassQuery q) {
		return null;
	}
}
