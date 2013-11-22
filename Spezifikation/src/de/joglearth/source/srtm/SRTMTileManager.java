package de.joglearth.source.srtm;

import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;


public final class SRTMTileManager implements Source<SRTMTileIndex, SRTMTile> {

    private static SRTMTileManager instance = null;
    
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
        return null;
    }

}
