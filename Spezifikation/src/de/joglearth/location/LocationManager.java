package de.joglearth.location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.ScreenCoordinates;
import de.joglearth.geometry.SurfaceListener;
import de.joglearth.geometry.Tile;
import de.joglearth.location.nominatim.NominatimManager;
import de.joglearth.location.nominatim.NominatimQuery;
import de.joglearth.location.nominatim.NominatimSource;
import de.joglearth.location.overpass.OverpassSource;
import de.joglearth.settings.Settings;
import de.joglearth.settings.SettingsContract;
import de.joglearth.settings.SettingsListener;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.SourceResponseType;
import de.joglearth.source.caching.MemoryCache;
import de.joglearth.source.caching.RequestDistributor;
import de.joglearth.source.caching.UnityMeasure;


/**
 * Administers the visibility of particular points gathered from
 * {@link de.joglearth.location.overpass.OverpassSource} and
 * {@link de.joglearth.location.nominatim.NominatimSource}, and user marks from
 * {@link de.joglearth.settings.Settings}.
 */
public class LocationManager {

    private NominatimQuery lastSearch;
    private boolean[] selectedResults;
    private boolean[] selectedUserTags;
    private boolean[] selectedPOIs;
    private Set<SurfaceListener> surfaceListeners;
    private Set<LocationListener> locationListeners;
    private NominatimManager nominatimManager;
    private Collection<Location> lastSearchLocations;
    private RequestDistributor<NominatimQuery, Collection<Location>> nominatimReqDistributor;
    private Set<LocationType> activeLocationTypes;


    private class UserTagsListener implements SettingsListener {

        @Override
        public void settingsChanged(String key, Object valOld, Object valNew) {
            
        }
    }


    /**
     * Constructor. Initializes the {@link LocationManager} and its underlying caches.
     */
    public LocationManager() {
        surfaceListeners = new HashSet<SurfaceListener>();
        locationListeners = new HashSet<LocationListener>();
        lastSearchLocations = new ArrayList<Location>();
        nominatimManager = NominatimManager.getInstance();
        nominatimReqDistributor = new RequestDistributor<NominatimQuery, Collection<Location>>(new UnityMeasure<Collection<Location>>());
        nominatimReqDistributor.addCache(new MemoryCache<NominatimQuery, Collection<Location>>(), 1000);
        nominatimReqDistributor.setSource(nominatimManager);
        activeLocationTypes = new HashSet<LocationType>();
        Settings.getInstance().addSettingsListener(SettingsContract.USER_LOCATIONS, new UserTagsListener());
        
    }

    /**
     * Changes the visibility of a given {@link de.joglearth.location.LocationType}.
     * 
     * @param type The <code>LocationType</code> that should be shown or hidden
     * @param active The <code>LocationType</code> to show or hide
     */
    public void setLocationTypeActive(LocationType type, boolean active) {
        if (active) {
            activeLocationTypes.add(type);
        } else {
            activeLocationTypes.remove(type);
        }
    }

    /**
     * Returns all active locations within the given area.
     * 
     * @param area The tiles defining the area
     * @return A collection of active locations
     */
    public Collection<Location> getActiveLocations(Iterable<Tile> area) {
        Collection<Location> locations = new ArrayList<Location>();
        for (Iterator<Location> iterator = lastSearchLocations.iterator(); iterator.hasNext();) {
            Location location = iterator.next();
            if (activeLocationTypes.contains(location.type)) {
                locations.add(location);
            }
        }
        Set<Location> userLocations = Settings.getInstance().getLocations(SettingsContract.USER_LOCATIONS);
        for (Location location : userLocations) {
            if (activeLocationTypes.contains(location.type)) {
                locations.add(location);
            }
        }
        return locations;
    }

    /**
     * Searches on the whole globe/map after a query string.
     * 
     * @param query The query string
     */
    public void searchGlobal(String query) {
        lastSearchLocations.clear();
        NominatimQuery nominatimQuery = new NominatimQuery(NominatimQuery.Type.GLOBAL);
        nominatimQuery.query = query;
        SourceResponse<Collection<Location>> response = nominatimReqDistributor.requestObject(
                nominatimQuery, new SourceListener<NominatimQuery, Collection<Location>>() {

                    @Override
                    public void requestCompleted(NominatimQuery key, Collection<Location> value) {
                        lastSearchLocations.addAll(value);
                    }
                });

        if (response.response != SourceResponseType.ASYNCHRONOUS
                && response.response != SourceResponseType.MISSING) {
            lastSearchLocations.addAll(lastSearchLocations);

        }

    }

    /**
     * Searches after a query string on the visible part of the map/globe.
     * 
     * @param query The query string
     * @param area A collection of tiles where the search should be performed on
     */
    public void searchLocal(String query, Iterable<Tile> area) {
        lastSearchLocations.clear();
        for (Tile t : area) {
            NominatimQuery nominatimQuery = new NominatimQuery(NominatimQuery.Type.LOCAL);
            nominatimQuery.query = query;
            nominatimQuery.area = t;
            SourceResponse<Collection<Location>> response = nominatimReqDistributor.requestObject(
                    nominatimQuery, new SourceListener<NominatimQuery, Collection<Location>>() {

                        @Override
                        public void requestCompleted(NominatimQuery key, Collection<Location> value) {
                            lastSearchLocations.addAll(value);
                        }
                    });

            if (response.response != SourceResponseType.ASYNCHRONOUS
                    && response.response != SourceResponseType.MISSING) {

                lastSearchLocations.addAll(lastSearchLocations);

            }
        }
    }

    /**
     * Gets the details of a point with given {@link de.joglearth.geometry.ScreenCoordinates}. To
     * achieve that it asks the {@link de.joglearth.source.caching.RequestDistributor}.
     * 
     * @param coordinates The <code>GeoCoordinates</code> of the point
     * @return The <code>Location</code> with details that is located on the given point or a
     *         <code>Location</code> without details if the details are not yet loaded.
     */
    public Location getDetails(GeoCoordinates coordinates) {
        NominatimQuery nominatimQuery = new NominatimQuery(NominatimQuery.Type.POINT);
        nominatimQuery.point = coordinates;
        SourceResponse<Collection<Location>> response = nominatimReqDistributor.requestObject(
                nominatimQuery, new SourceListener<NominatimQuery, Collection<Location>>() {

                    @Override
                    public void requestCompleted(NominatimQuery key, Collection<Location> value) {
                    }
                });

        if (response.response != SourceResponseType.ASYNCHRONOUS
                && response.response != SourceResponseType.MISSING) {
            if (response.value.size() > 0)
                return response.value.iterator().next();
        }
        // TODO: ??
        return new Location(null, null, null, null);
    }

    /**
     * Adds a new {@link SurfaceListener} that is called on every change of the surface.
     * 
     * @param l The new <code>SurfaceListener</code>
     */
    public void addSurfaceListener(SurfaceListener l) {
        surfaceListeners.add(l);
    }

    /**
     * Removes a specific {@link SurfaceListener}.
     * 
     * @param l The <code>SurfaceListener</code> that should be removed
     */
    public void removeSurfaceListener(SurfaceListener l) {
        surfaceListeners.remove(l);
    }

    /**
     * Adds a new {@link LocationListener} that is called when the search results are available.
     * 
     * @param l The new <code>LocationListener</code>
     */
    public void addLocationListener(LocationListener l) {
        locationListeners.add(l);
    }

    /**
     * Removes a specific {@link LocationListener}.
     * 
     * @param l The <code>LocationListener</code> that should be removed
     */
    public void removeLocationListener(LocationListener l) {
        locationListeners.remove(l);

    }
}
