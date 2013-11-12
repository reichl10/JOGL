package de.joglearth.source;

import de.joglearth.geometry.Tile;
import de.joglearth.surface.Location;
import de.joglearth.rendering.*;
import de.joglearth.source.*;
import de.joglearth.ui.*;

public class NominatimSource implements Source<NominatimQuery, Location[]> {

	public NominatimSource(SourceListener<NominatimQuery, Location[]> owner) {
	}

	@Override
	public SourceResponse<Location[]> requestObject(NominatimQuery key,
			SourceListener<NominatimQuery, Location[]> sender) {
		// TODO Automatisch erstellter Methoden-Stub
		return null;
	}

}