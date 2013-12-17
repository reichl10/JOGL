package de.joglearth.source.osm;

import de.joglearth.settings.Settings;
import de.joglearth.settings.SettingsContract;
import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.caching.Cache;
import de.joglearth.source.caching.MemoryCache;
import de.joglearth.source.caching.RequestDistributor;


/**
 * Singleton class that retrieves data from the {@link de.joglearth.source.osm.OSMTileSource}.
 */
public final class OSMTileManager implements Source<OSMTile, byte[]> {

    private RequestDistributor<OSMTile, byte[]> dist;
    private Cache<OSMTile, byte[]> cache;

    private static OSMTileManager               instance = null;


    /**
     * Returns the instance of the class or creates it, if it does not exist yet.
     * 
     * @return The instance of <code>OSMTileManager</code>
     */
    public static OSMTileManager getInstance() {
        if (instance == null) {
            instance = new OSMTileManager();
        }
        return instance;
    }

    // Default constructor
    private OSMTileManager() {
        dist = new RequestDistributor<OSMTile, byte[]>();
        cache = new MemoryCache<OSMTile, byte[]>();
        //TODO Richtige Standardgröße? Nur ein Cache?
        dist.addCache(cache, Settings.getInstance().getInteger(SettingsContract.CACHE_SIZE_MEMORY));
    }

    @Override
    public SourceResponse<byte[]> requestObject(OSMTile key,
            SourceListener<OSMTile, byte[]> sender) {
        return dist.requestObject(key, sender);
    }

    /**
     * Sets the size of a {@link de.joglearth.source.caching.Cache}.
     * 
     * @param cacheSize the new size of the <code>Cache</code>
     */
    public void setCacheSize(int cacheSize) {
        //TODO welcher Cache
        dist.setCacheSize(cache, cacheSize);
    }

}
