package de.joglearth.source.srtm;

import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.caching.ByteArrayMeasure;
import de.joglearth.source.caching.FileSystemCache;
import de.joglearth.source.caching.MemoryCache;
import de.joglearth.source.caching.RequestDistributor;
import de.joglearth.source.caching.UnityMeasure;
import de.joglearth.util.ApplicationData;


/**
 * Singleton class that retrieves data from the {@link de.joglearth.source.srtm.SRTMTileSource}.
 */
public final class SRTMTileManager implements Source<SRTMTileName, SRTMTile> {

    private static SRTMTileManager                      instance = null;

    private RequestDistributor<SRTMTileName, SRTMTile> tileRequestDistributor;
    private RequestDistributor<SRTMTileName, byte[]> binaryRequestDistributor;


    /**
     * Returns the instance of the class or creates it, if it does not exist yet.
     * 
     * @return The instance of <code>SRTMTileManager</code>
     */
    public static SRTMTileManager getInstance() {
        if (instance == null) {
            instance = new SRTMTileManager(ApplicationData.getDirectory("srtm"), 10*1024*1024, 
                    10*1024*1024);
        }
        return instance;
    }
    
    public static void shutDown() {
        if (instance != null) {
            instance.dispose();
        }
    }

    // Default constructor
    private SRTMTileManager(String folder, int memCacheBytes, int fsCacheBytes) {
        SRTMBinarySource binarySource = new SRTMBinarySource();
        FileSystemCache<SRTMTileName> binaryCache 
            = new FileSystemCache<>(folder, new SRTMPathTranslator());
        binaryRequestDistributor = new RequestDistributor<>(new ByteArrayMeasure());
        binaryRequestDistributor.setSource(binarySource);
        binaryRequestDistributor.addCache(binaryCache, fsCacheBytes);
        
        SRTMTileSource tileSource = new SRTMTileSource(binaryRequestDistributor);
        MemoryCache<SRTMTileName, SRTMTile> tileCache = new MemoryCache<>();
        tileRequestDistributor = new RequestDistributor<>(new UnityMeasure<SRTMTile>());
        tileRequestDistributor.setSource(tileSource);
        tileRequestDistributor.addCache(tileCache, memCacheBytes / SRTMTile.SIZE_IN_MEMORY);
    }

    @Override
    public SourceResponse<SRTMTile> requestObject(SRTMTileName key,
            SourceListener<SRTMTileName, SRTMTile> sender) {
        return tileRequestDistributor.requestObject(key, sender);
    }

    @Override
    public void dispose() {
        tileRequestDistributor.dispose();
        binaryRequestDistributor.dispose();
    }
}
