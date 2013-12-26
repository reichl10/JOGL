package de.joglearth.source.osm;

import de.joglearth.settings.Settings;
import de.joglearth.settings.SettingsContract;
import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.TileName;
import de.joglearth.source.caching.ByteArrayMeasure;
import de.joglearth.source.caching.Cache;
import de.joglearth.source.caching.FileSystemCache;
import de.joglearth.source.caching.MemoryCache;
import de.joglearth.source.caching.RequestDistributor;
import de.joglearth.util.ApplicationData;


/**
 * Singleton class that retrieves data from the {@link de.joglearth.source.osm.OSMTileSource}.
 */
public final class OSMTileManager implements Source<TileName, byte[]> {

    private RequestDistributor<TileName, byte[]> dist;
    private Cache<TileName, byte[]> memoryCache;
    private Cache<TileName, byte[]> fsCache;

    private static OSMTileManager instance = null;


    /**
     * Returns the instance of the class or creates it, if it does not exist yet.
     * 
     * @return The instance of <code>OSMTileManager</code>
     */
    public static OSMTileManager getInstance() {
        if (instance == null) {
            int memoryCacheSize = (int) ((Settings.getInstance()
                    .getInteger(SettingsContract.CACHE_SIZE_MEMORY)) * 0.7);
            int fsCacheSize = (int) ((Settings.getInstance()
                    .getInteger(SettingsContract.CACHE_SIZE_FILESYSTEM)) * 0.7);
            instance = new OSMTileManager(ApplicationData.getDirectory("osm"), memoryCacheSize,
                    fsCacheSize);
        }
        return instance;
    }
    
    public static void shutDown() {
        if (instance != null) {
            instance.dispose();
        }
    }

    // Default constructor
    private OSMTileManager(String cachePath, int memoryCacheSize, int fsCacheSize) {
        dist = new RequestDistributor<TileName, byte[]>(new ByteArrayMeasure());
        memoryCache = new MemoryCache<TileName, byte[]>();
        fsCache = new FileSystemCache<TileName>(cachePath, new OSMPathTranslator());
        dist.setSource(new OSMTileSource());
        dist.addCache(memoryCache, memoryCacheSize);
        dist.addCache(fsCache, fsCacheSize);
    }

    @Override
    public SourceResponse<byte[]> requestObject(TileName key,
            SourceListener<TileName, byte[]> sender) {
        return dist.requestObject(key, sender);
    }

    /**
     * Sets the size of a {@link de.joglearth.source.caching.MemoryCache}.
     * 
     * @param cacheSize the new size of the <code>Cache</code>
     */
    public void setMemoryCacheSize(int cacheSize) {
        dist.setCacheSize(memoryCache, cacheSize);
    }
    
    /**
     * Sets the size of a {@link de.joglearth.source.caching.FileSystemCache}.
     * 
     * @param cacheSize the new size of the <code>Cache</code>
     */
    public void setFileSystemCacheSize(int cacheSize) {
        dist.setCacheSize(fsCache, cacheSize);
    }
    
    @Override
    public void dispose() {
        dist.dispose();
    }
}
