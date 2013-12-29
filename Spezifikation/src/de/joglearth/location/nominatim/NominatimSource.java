package de.joglearth.location.nominatim;

import java.util.Collection;

import de.joglearth.location.Location;
import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.util.HTTP;


/**
 * Provides responses of search requests, for e.g. search requests for places or detailed
 * information to a point. The response will be prepared for the {@link de.joglearth.location.LocationManager}; uses the
 * {@link de.joglearth.util.HTTP} for the search request.
 * 
 */
public class NominatimSource implements Source<NominatimQuery, Collection<Location>> {

    /**
     * Constructor. Initializes the {@link de.joglearth.location.nominatim.NominatimSource}.
     */
    public NominatimSource() {

    }

    @Override
    public SourceResponse<Collection<Location>> requestObject(NominatimQuery key,
            SourceListener<NominatimQuery, Collection<Location>> sender) {
        return null;
    }

    @Override
    public void dispose() {
        
    }
}
