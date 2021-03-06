package de.joglearth.location.overpass;

import java.util.Collection;

import de.joglearth.location.Location;
import de.joglearth.source.Priorized;
import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.caching.Cache;
import de.joglearth.source.caching.MemoryCache;
import de.joglearth.source.caching.RequestDistributor;


/**
 * Singleton class that retrieves data from the {@link OverpassSource}.
 */
public final class OverpassManager implements Source<OverpassQuery, Collection<Location>>, 
        Priorized {

    private static OverpassManager instance;
    private RequestDistributor<OverpassQuery, Collection<Location>> dist;
    private OverpassSource source;
    private Cache<OverpassQuery, Collection<Location>> cache;


    /* Default constructor */
    private OverpassManager() {
        dist = new RequestDistributor<OverpassQuery, Collection<Location>>();
        source = new OverpassSource();
        cache = new MemoryCache<OverpassQuery, Collection<Location>>();

        dist.setSource(source);
        dist.addCache(cache, 1000);
    }

    /**
     * Returns the instance of the class or creates it, if it does not exist yet.
     * 
     * @return The instance of <code>OverpassManager</code>
     */
    public static OverpassManager getInstance() {
        if (instance == null) {
            instance = new OverpassManager();
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

    @Override
    public SourceResponse<Collection<Location>> requestObject(OverpassQuery key,
            SourceListener<OverpassQuery, Collection<Location>> sender) {
        return dist.requestObject(key, sender);
    }

    /**
     * Sets the size of a {@link Cache}.
     * 
     * @param size The new size of the <code>Cache</code>
     */
    public void setCacheSize(int size) {
        dist.setCacheSize(cache, size);
    }

    @Override
    public void dispose() {
        dist.dispose();
    }

    @Override
    public void increasePriority() {
        source.increasePriority();
    }
}
