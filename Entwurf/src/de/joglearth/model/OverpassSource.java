package de.joglearth.model;

import de.joglearth.model.*;
import de.joglearth.view.*;
import de.joglearth.controller.*;

public class OverpassSource extends Source<OverpassQuery, Location[]> {
	public OverpassSource(RequestListener<OverpassQuery, Location[]> owner) {
		super(owner);
	}

	@Override
	public Location[] requestObject(OverpassQuery q) {
		return null;
	}
}
