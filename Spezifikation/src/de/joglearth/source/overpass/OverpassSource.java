package de.joglearth.source.overpass;

import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
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
