package de.joglearth.source;

import de.joglearth.surface.Location;


/**
 * The class NominatimSource provides responses of search requests, for e.g. search requests for
 * places or detailed information to a point. The response will be prepared for the LocationManager.
 * This class uses the HTTPUtils for the search request.
 * 
 */
public class NominatimSource implements Source<NominatimQuery, Location[]> {

    /**
     * Constructor NominatimSource.
     * 
     * @param owner
     */
    public NominatimSource(SourceListener<NominatimQuery, Location[]> owner) {}

    /**
     * 
     * @return
     */
    @Override
    public SourceResponse<Location[]> requestObject(NominatimQuery key,
            SourceListener<NominatimQuery, Location[]> sender) {
        return null;
    }
}
