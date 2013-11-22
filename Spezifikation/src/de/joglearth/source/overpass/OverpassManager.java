package de.joglearth.source.overpass;

import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.caching.RequestDistributor;
import de.joglearth.surface.Location;


public final class OverpassManager implements Source<OverpassQuery, Location[]>{

    private static OverpassManager instance;
    
    private RequestDistributor<OverpassQuery, Location[]> dist;
    
    public static OverpassManager getInstance() {
        if (instance == null) {
            instance = new OverpassManager();
        }
        return instance;
    }
    
    private OverpassManager() {
        
    }
    
    @Override
    public SourceResponse<Location[]> requestObject(OverpassQuery key,
            SourceListener<OverpassQuery, Location[]> sender) {
        return dist.requestObject(key, sender);
    }
    
}
