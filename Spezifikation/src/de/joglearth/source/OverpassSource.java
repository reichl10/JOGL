package de.joglearth.source;

import de.joglearth.surface.Location;

/**
 * Provides responses from the OverpassAPI, for e.g. detailed information to a POI or a place.
 * 
 */
public class OverpassSource implements Source<OverpassQuery, Location[]> {

    @Override
    public SourceResponse<Location[]> requestObject(OverpassQuery key,
            SourceListener<OverpassQuery, Location[]> sender) {
        return null;
    }
}
