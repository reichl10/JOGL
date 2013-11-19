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


/**
 * Administers the visibility of particular points gathered from <code>OverpassSource</code> and
 * <code>NominatimSource</code>, and user marks from <code>Settings</code>.
 */
public class LocationManager implements SettingsListener {

    private MemoryCache<NominatimQuery, Location[]> nominatimCache;
    private MemoryCache<OverpassQuery, Location[]>  overpassCache;
    private NominatimQuery                          lastSearch;
    private boolean[]                               selectedResults, selectedUserTags,
                                                    selectedPOIs;
    private Settings                                settings;


    /**
     * Constructor. Initializes the <code>LocationManager</code> and its underlying caches.
     */
    public LocationManager() {
        this.settings = Settings.getInstance();
        OverpassSource overpass = null;
        NominatimSource nominatim = null;
        overpassCache = new MemoryCache<OverpassQuery, Location[]>();
        nominatimCache = new MemoryCache<NominatimQuery, Location[]>();
    }

    /**
     * Changes the visibility of a search result location.
     * 
     * @param index The index of the search result that should be displayed.
     * @param enable Whether to enable or disable the search result.
     */
    public void enableResult(int index, boolean enable) {}

    /**
     * TODO ?
     * 
     * @param index
     * @param enable
     */
    public void enableUserTag(int index, boolean enable) {}

    /**
     * Changes the visibility of a <code>POI</code>.
     * 
     * @param type The <code>LocationType</code> of the <code>POI</code> that should be shown
     * @param enable Whether to enable or disable the <code>POI</code>.
     */
    public void enablePOI(LocationType type, boolean enable) {}

    /**
     * Searches on the whole globe/map after a query string.
     * 
     * @param query The query string.
     */
    public void searchGlobal(String query) {

    }

    /**
     * Searches after a query string on the visible part of the map/globe.
     * 
     * @param query The query string.
     * @param area An array of visible tiles where the search should be performed on
     */
    public void searchLocal(String query, Tile[] area) {

    }

    /**
     * 
     * @param coordinates
     * @return
     */
    public Location getDetails(ScreenCoordinates coordinates) {
        return null;
    }

    @Override
    public void settingsChanged(String key, Object valOld, Object valNew) {

    }

    /**
     * Adds a new <code>SurfaceListener</code> that is called on every change of the surface.
     * 
     * @param l The new <code>SurfaceListener</code>
     */
    public void addSurfaceListener(SurfaceListener l) {

    }

    /**
     * Removes a specific <code>SurfaceListener</code>.
     * 
     * @param l The <code>SurfaceListener</code> that should be removed.
     */
    public void removeSurfaceListener(SurfaceListener l) {

    }

    /**
     * Adds a new <code>LocationListener</code> that is called when the search results are
     * available.
     * 
     * @param l The new <code>LocationListener</code>.
     */
    public void addLocationListener(LocationListener l) {

    }

    /**
     * Removes a specific <code>LocationListener</code>.
     * 
     * @param l The <code>LocationListener</code> that should be removed.
     */
    public void removeLocationListener(LocationListener l) {

    }
}
