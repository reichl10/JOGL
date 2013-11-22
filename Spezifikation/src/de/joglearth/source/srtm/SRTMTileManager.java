package de.joglearth.source.srtm;

import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.caching.RequestDistributor;


public final class SRTMTileManager implements Source<SRTMTileIndex, SRTMTile> {

    private static SRTMTileManager instance = null;
    
    private RequestDistributor<SRTMTileIndex, SRTMTile> dist;
    
    public SRTMTileManager getInstance() {
        if (instance == null) {
            instance = new SRTMTileManager();
        }
        return instance;
    }
    
    private SRTMTileManager() {
        
    }
    
    @Override
    public SourceResponse<SRTMTile> requestObject(SRTMTileIndex key,
            SourceListener<SRTMTileIndex, SRTMTile> sender) {
        return dist.requestObject(key, sender);
    }

}
