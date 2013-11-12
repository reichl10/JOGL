package de.joglearth.source;

import de.joglearth.surface.Location;


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
