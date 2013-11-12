package de.joglearth.surface;


import de.joglearth.caching.MemoryCache;
import de.joglearth.geometry.Point;
import de.joglearth.geometry.Tile;
import de.joglearth.settings.Settings;
import de.joglearth.settings.SettingsListener;
import de.joglearth.source.NominatimQuery;
import de.joglearth.source.NominatimSource;
import de.joglearth.source.OverpassQuery;
import de.joglearth.source.OverpassSource;
import de.joglearth.source.SourceListener;


public class LocationManager implements SettingsListener {
	
	private MemoryCache<NominatimQuery, Location[]> nominatimCache;
	private MemoryCache<OverpassQuery, Location[]> overpassCache;
	private NominatimQuery lastSearch;
	private boolean[] selectedResults, selectedUserTags, selectedPOIs;
	private Settings settings;

	public LocationManager() {
		this.settings = Settings.getInstance();
		OverpassSource overpass = null;
		NominatimSource nominatim = null;
		overpassCache = new MemoryCache<OverpassQuery, Location[]>();
		nominatimCache = new MemoryCache<NominatimQuery, Location[]>();
	}

	

	public void enableResult(int index, boolean enable) {
	}

	public void enableUserTag(int index, boolean enable) {
	}

	public void enablePOI(LocationType type, boolean enable) {
	}

	public void search(String query) {

	}

	public void search(String query, Tile[] area) {

	}

	public Location getDetails(Point coordinates) {
		return null;
	}

	private class NominatimListener implements
			SourceListener<NominatimListener, Location[]> {

		@Override
		public void requestCompleted(NominatimListener n, Location[] l) {
		}
	}

	private class OverpassListener implements
			SourceListener<OverpassListener, Location[]> {

		@Override
		public void requestCompleted(OverpassListener o, Location[] l) {
		}
	}



	@Override
	public void settingsChanged(String key, Object valOld, Object valNew) {
		
	}
}