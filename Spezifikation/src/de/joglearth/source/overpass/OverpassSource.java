package de.joglearth.source.overpass;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.surface.Location;
import de.joglearth.surface.LocationType;


/**
 * Provides responses from the OverpassAPI, for e.g. detailed information to a POI or a place.
 */
public class OverpassSource implements Source<OverpassQuery, Collection<Location>> {

    /* Location = POI */
    private Map<LocationType, String> locationRequest;
    private final ExecutorService executor;
    
    
    public OverpassSource(){
        executor = Executors.newFixedThreadPool(2);
        
        
        
        
        locationRequest = new HashMap<>();
        
        
    }
    
    
    @Override
    public SourceResponse<Collection<Location>> requestObject(OverpassQuery key,
            SourceListener<OverpassQuery, Collection<Location>> sender) {
        return null;
    }

    @Override
    public void dispose() {
        // TODO Automatisch generierter Methodenstub
        
    }
    
    
}
