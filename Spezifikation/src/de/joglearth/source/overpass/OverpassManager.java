package de.joglearth.source.overpass;

import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.caching.RequestDistributor;
import de.joglearth.source.nominatim.NominatimManager;
import de.joglearth.source.nominatim.NominatimSource;
import de.joglearth.surface.Location;


/**
 * Singleton class that retrieves data from the {@link OverpassSource}.
 */
public final class OverpassManager implements Source<OverpassQuery, Location[]> {

    private static OverpassManager                        instance;

    private RequestDistributor<OverpassQuery, Location[]> dist;


    /**
     * Returns the instance of the class or creates it, if it does not exist yet.
     * 
     * @return The instance of {@link OverpassManager}
     */
    public static OverpassManager getInstance() {
        if (instance == null) {
            instance = new OverpassManager();
        }
        return instance;
    }

    // Default constructor.
    private OverpassManager() {

    }

    @Override
    public SourceResponse<Location[]> requestObject(OverpassQuery key,
            SourceListener<OverpassQuery, Location[]> sender) {
        return dist.requestObject(key, sender);
    }
}
