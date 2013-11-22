package de.joglearth.source.nominatim;

import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.surface.Location;


public final class NominatimManager implements Source<NominatimQuery, Location[]> {
    private static NominatimManager instance = null;
    
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
        // TODO Automatisch generierter Methodenstub
        return null;
    }
    
}
