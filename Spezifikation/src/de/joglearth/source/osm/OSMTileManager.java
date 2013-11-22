package de.joglearth.source.osm;

import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.caching.RequestDistributor;


public class OSMTileManager implements Source<OSMTile, byte[]> {

    private RequestDistributor<OSMTile, byte[]> dist;

    private static OSMTileManager instance = null;


    public static OSMTileManager getInstance() {
        if (instance == null) {
            instance = new OSMTileManager();
        }
        return instance;
    }

    private OSMTileManager() {

    }

    @Override
    public SourceResponse<byte[]> requestObject(OSMTile key,
            SourceListener<OSMTile, byte[]> sender) {
        return dist.requestObject(key, sender);
    }

    
    public void setCacheSize(int cacheSize) {
        
    }
    
}
