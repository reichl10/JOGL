package de.joglearth.location.nominatim;

import java.util.Collection;

import de.joglearth.location.Location;
import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.caching.Cache;
import de.joglearth.source.caching.MemoryCache;
import de.joglearth.source.caching.RequestDistributor;


/**
 * Singleton class that retrieves data from the {@link NominatimSource}.
 */
public final class NominatimManager implements Source<NominatimQuery, Collection<Location>> {

    private static NominatimManager instance = null;
    private Cache<NominatimQuery, Collection<Location>> cache;
    private Source<NominatimQuery, Collection<Location>> source;
    private RequestDistributor<NominatimQuery, Collection<Location>> dist;


    /**
     * Returns the instance of the class or creates it, if it does not exist yet.
     * 
     * @return The instance of <code>NominatimManager</code>
     */
    public static NominatimManager getInstance() {
        if (instance == null) {
            instance = new NominatimManager();
        }
        return instance;
    }

    /**
     *  Called if the program has been closed to terminate all pending processes.
     */
    public static void shutDown() {
        if (instance != null) {
            instance.dispose();
        }
    }

    // Constructor.
    private NominatimManager() {
        dist = new RequestDistributor<NominatimQuery, Collection<Location>>();
        cache = new MemoryCache<NominatimQuery, Collection<Location>>();
        source = new NominatimSource();
        dist.addCache(cache, 100000);
        dist.setSource(source);

    }

    @Override
    public SourceResponse<Collection<Location>> requestObject(NominatimQuery key,
            SourceListener<NominatimQuery, Collection<Location>> sender) {
        return dist.requestObject(key, sender);
    }

    /**
     * Sets the size of a {@link Cache}.
     * 
     * @param cacheSize The new size of the <code>Cache</code>
     */
    public void setCacheSize(int cacheSize) {
        dist.setCacheSize(cache, cacheSize);
    }

    @Override
    public void dispose() {
        if (instance != null) {
            dist.dispose();
        }
    }
}
