package de.joglearth.source.overpass;

import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.surface.Location;


public final class OverpassManager implements Source<OverpassQuery, Location[]>{

    private static OverpassManager instance;
    
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
        // TODO Automatisch generierter Methodenstub
        return null;
    }
    
}
