package de.joglearth.source.osm;

import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.caching.RequestDistributor;

/**
 * Singleton class that retrieves data from the {@link OSMTileSource}.
 */
public final class OSMTileManager implements Source<OSMTile, byte[]> {

    private RequestDistributor<OSMTile, byte[]> dist;

    private static OSMTileManager instance = null;

    /**
     * Returns the instance of the class or creates it, if it does not exist yet.
     * 
     * @return The instance of {@link OSMTileManager}
     */
    public static OSMTileManager getInstance() {
        if (instance == null) {
            instance = new OSMTileManager();
        }
        return instance;
    }

    //Default constructor
    private OSMTileManager() {

    }

    @Override
    public SourceResponse<byte[]> requestObject(OSMTile key,
            SourceListener<OSMTile, byte[]> sender) {
        return dist.requestObject(key, sender);
    }


    /**
     * Sets the size of a {@link Cache}.
     * @param cacheSize the new size of the <code>Cache</code>
     */
    public void setCacheSize(int cacheSize) {
        
    }
    
}
