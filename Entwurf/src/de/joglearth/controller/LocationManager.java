package de.joglearth.controller;


import de.joglearth.model.*;
import de.joglearth.view.*;
import de.joglearth.controller.*;

public class LocationManager extends UpdateProvider implements UpdateListener {
	
	private MemoryCache<NominatimQuery, Location[]> nominatimCache;
	private MemoryCache<OverpassQuery, Location[]> overpassCache;
	private NominatimQuery lastSearch;
	private boolean[] selectedResults, selectedUserTags, selectedPOIs;
	private Settings settings;

	public LocationManager(Settings settings) {
		this.settings = settings;
		OverpassSource overpass = null;
		NominatimSource nominatim = null;
		overpassCache = new MemoryCache<OverpassQuery, Location[]>(null, null,
				overpass);
		nominatimCache = new MemoryCache<NominatimQuery, Location[]>(null,
				null, nominatim);
		settings.addUpdateListener(this);
	}

	

	public void enableResult(int index, boolean enable) {
		postUpdate();
	}

	public void enableUserTag(int index, boolean enable) {
		postUpdate();
	}

	public void enablePOI(PoiType type, boolean enable) {
		postUpdate();
	}

	public void search(String query) {

	}

	public void search(String query, Tile[] area) {

	}

	public Location getDetails(Point coordinates) {
		return null;
	}

	private class NominatimListener implements
			RequestListener<NominatimListener, Location[]> {

		@Override
		public void requestCompleted(NominatimListener n, Location[] l) {
			postUpdate();
		}
	}

	private class OverpassListener implements
			RequestListener<OverpassListener, Location[]> {

		@Override
		public void requestCompleted(OverpassListener o, Location[] l) {
			postUpdate();
		}
	}

	//settings haben sich geändert, selectedUserTags anpassen?
	@Override
	public void post() {
		
	}
}