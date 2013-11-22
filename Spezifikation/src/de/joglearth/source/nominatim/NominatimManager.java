package de.joglearth.source.nominatim;

import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.caching.RequestDistributor;
import de.joglearth.surface.Location;


public final class NominatimManager implements Source<NominatimQuery, Location[]> {
    
    private static NominatimManager instance = null;
    
    private RequestDistributor<NominatimQuery, Location[]> dist;
    
    public static NominatimManager getInstance() {
        if (instance == null) {
            instance = new NominatimManager();
        }
        return instance;
    }
    
    private NominatimManager() {
        
    }

    @Override
    public SourceResponse<Location[]> requestObject(NominatimQuery key,
            SourceListener<NominatimQuery, Location[]> sender) {
        return dist.requestObject(key, sender);
    }
    
    public void setCacheSize(int cacheSize) {
        
    }
    
}
