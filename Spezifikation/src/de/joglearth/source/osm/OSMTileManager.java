package de.joglearth.source.osm;

import de.joglearth.settings.Settings;
import de.joglearth.settings.SettingsContract;
import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.caching.Cache;
import de.joglearth.source.caching.FileSystemCache;
import de.joglearth.source.caching.MemoryCache;
import de.joglearth.source.caching.RequestDistributor;


/**
 * Singleton class that retrieves data from the {@link de.joglearth.source.osm.OSMTileSource}.
 */
public final class OSMTileManager implements Source<OSMTile, byte[]> {

    private RequestDistributor<OSMTile, byte[]> dist;
    private Cache<OSMTile, byte[]> memoryCache;
    private Cache<OSMTile, byte[]> fsCache;

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
        memoryCache = new MemoryCache<OSMTile, byte[]>();
        //TODO testen obs so passt -> Constantin fragen?!
        fsCache = new FileSystemCache("osm/", new OSMPathTranslator());
        Integer cacheSizeMemory = Settings.getInstance().getInteger(SettingsContract.CACHE_SIZE_MEMORY);
        Integer cacheSizeFileSystem = Settings.getInstance().getInteger(SettingsContract.CACHE_SIZE_FILESYSTEM);
        dist.addCache(memoryCache, (int) ((Settings.getInstance().getInteger(SettingsContract.CACHE_SIZE_MEMORY))*0.7));
        dist.addCache(fsCache, (int) ((Settings.getInstance().getInteger(SettingsContract.CACHE_SIZE_FILESYSTEM))*0.7));

    }

    @Override
    public SourceResponse<byte[]> requestObject(OSMTile key,
            SourceListener<OSMTile, byte[]> sender) {
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

}
