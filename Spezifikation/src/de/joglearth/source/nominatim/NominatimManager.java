package de.joglearth.source.nominatim;

import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.caching.RequestDistributor;
import de.joglearth.surface.Location;

/**
 * Singleton class that retrieves data from the {@link NominatimSource}.
 */
public final class NominatimManager implements Source<NominatimQuery, Location[]> {
    
    private static NominatimManager instance = null;
    
    private RequestDistributor<NominatimQuery, Location[]> dist;
    
    /**
     * Returns the instance of the class or creates it, if it does not exist yet.
     * 
     * @return The instance of {@link NominatimManager}
     */
    public static NominatimManager getInstance() {
        if (instance == null) {
            instance = new NominatimManager();
        }
        return instance;
    }
    
    //Constructor.
    private NominatimManager() {
        
    }

    @Override
    public SourceResponse<Location[]> requestObject(NominatimQuery key,
            SourceListener<NominatimQuery, Location[]> sender) {
        return dist.requestObject(key, sender);
    }
    
    /**
     * Sets the size of a {@link Cache}.
     * @param cacheSize The new size of the <code>Cache</code>
     */
    public void setCacheSize(int cacheSize) {
        
    }
}