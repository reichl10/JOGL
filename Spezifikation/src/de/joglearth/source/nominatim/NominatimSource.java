package de.joglearth.source.nominatim;

import de.joglearth.source.HTTPUtils;
import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.surface.Location;


/**
 * Provides responses of search requests, for e.g. search requests for places or detailed
 * information to a point. The response will be prepared for the {@link LocationManager}; uses the
 * {@link HTTPUtils} for the search request.
 * 
 */
public class NominatimSource implements Source<NominatimQuery, Location[]> {

    /**
     * Constructor. Initializes the {@link NominatimSource}.
     */
    public NominatimSource() {

    }

    @Override
    public SourceResponse<Location[]> requestObject(NominatimQuery key,
            SourceListener<NominatimQuery, Location[]> sender) {
        return null;
    }
}
