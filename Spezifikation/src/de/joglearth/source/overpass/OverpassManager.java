package de.joglearth.source.overpass;

import java.util.Collection;

import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.caching.RequestDistributor;
import de.joglearth.source.nominatim.NominatimManager;
import de.joglearth.source.nominatim.NominatimSource;
import de.joglearth.surface.Location;


/**
 * Singleton class that retrieves data from the {@link de.joglearth.source.overpass.OverpassSource}.
 */
public final class OverpassManager implements Source<OverpassQuery, Collection<Location>> {

    private static OverpassManager                        instance;

    private RequestDistributor<OverpassQuery, Collection<Location>> dist;


    /**
     * Returns the instance of the class or creates it, if it does not exist yet.
     * 
     * @return The instance of <code>OverpassManager</code>
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
    public SourceResponse<Collection<Location>> requestObject(OverpassQuery key,
            SourceListener<OverpassQuery, Collection<Location>> sender) {
        return dist.requestObject(key, sender);
    }
}
