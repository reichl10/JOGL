package de.joglearth.surface;

import java.util.Collection;

import de.joglearth.geometry.ScreenCoordinates;
import de.joglearth.geometry.Tile;
import de.joglearth.settings.Settings;
import de.joglearth.settings.SettingsListener;
import de.joglearth.source.nominatim.NominatimQuery;
import de.joglearth.source.nominatim.NominatimSource;
import de.joglearth.source.overpass.OverpassSource;


/**
 * Administers the visibility of particular points gathered from
 * {@link de.joglearth.source.overpass.OverpassSource} and
 * {@link de.joglearth.source.nominatim.NominatimSource}, and user marks from
 * {@link de.joglearth.settings.Settings}.
 */
public class LocationManager {

    private NominatimQuery lastSearch;
    private boolean[]      selectedResults;
    private boolean[]      selectedUserTags;
    private boolean[]      selectedPOIs;


    private class UserTagsListener implements SettingsListener {

        @Override
        public void settingsChanged(String key, Object valOld, Object valNew) {}
    }


    /**
     * Constructor. Initializes the {@link LocationManager} and its underlying caches.
     */
    public LocationManager() {}

    /**
     * Changes the visibility of a given {@link de.joglearth.surface.LocationType}.
     * 
     * @param type The <code>LocationType</code> that should be shown or hidden
     * @param active The <code>LocationType</code> to show or hide
     */
    public void setLocationTypeActive(LocationType type, boolean active) {

    }

    /**
     * Returns all active locations within the given area.
     * 
     * @param area The tiles defining the area
     * @return A collection of active locations
     */
    public Collection<Location> getActiveLocations(Iterable<Tile> area) {
        return null;
    }

    /**
     * Searches on the whole globe/map after a query string.
     * 
     * @param query The query string
     */
    public void searchGlobal(String query) {

    }

    /**
     * Searches after a query string on the visible part of the map/globe.
     * 
     * @param query The query string
     * @param area A collection of tiles where the search should be performed on
     */
    public void searchLocal(String query, Iterable<Tile> area) {

    }

    /**
     * Gets the details of a point with given {@link de.joglearth.geometry.ScreenCoordinates}. To
     * achieve that it asks the {@link de.joglearth.source.caching.RequestDistributor}.
     * 
     * @param coordinates The <code>ScreenCoordinates</code> of the point
     * @return The <code>Location</code> with details that is located on the given point or a
     *         <code>Location</code> without details if the details are not yet loaded.
     */
    public Location getDetails(ScreenCoordinates coordinates) {
        return null;
    }

    /**
     * Adds a new {@link SurfaceListener} that is called on every change of the surface.
     * 
     * @param l The new <code>SurfaceListener</code>
     */
    public void addSurfaceListener(SurfaceListener l) {

    }

    /**
     * Removes a specific {@link SurfaceListener}.
     * 
     * @param l The <code>SurfaceListener</code> that should be removed
     */
    public void removeSurfaceListener(SurfaceListener l) {

    }

    /**
     * Adds a new {@link LocationListener} that is called when the search results are available.
     * 
     * @param l The new <code>LocationListener</code>
     */
    public void addLocationListener(LocationListener l) {

    }

    /**
     * Removes a specific {@link LocationListener}.
     * 
     * @param l The <code>LocationListener</code> that should be removed
     */
    public void removeLocationListener(LocationListener l) {

    }
}
