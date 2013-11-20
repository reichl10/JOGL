package de.joglearth.source;

import de.joglearth.surface.Location;

/**
 * Provides responses from the OverpassAPI, for e.g. detailed information to a POI or a place. The
 * response will be prepared for the {@link LocationManager}; uses the {@link HTTPUtils} for the
 * search request.
 * 
 */
public class OverpassSource implements Source<OverpassQuery, Location[]> {

    @Override
    public SourceResponse<Location[]> requestObject(OverpassQuery key,
            SourceListener<OverpassQuery, Location[]> sender) {
        return null;
    }
}
