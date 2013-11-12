package de.joglearth.surface;


import de.joglearth.caching.MemoryCache;
import de.joglearth.geometry.ScreenCoordinates;
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
	private SurfaceNotifier notifier;

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

	public Location getDetails(ScreenCoordinates coordinates) {
		return null;
	}


	@Override
	public void settingsChanged(String key, Object valOld, Object valNew) {
	
	}

	public void addSurfaceListener(SurfaceListener l) {
		notifier.addSurfaceListener(l);
	}
	
	public void removeSurfaceListener(SurfaceListener l) {
		notifier.removeSurfaceListener(l);
	}
	
}
