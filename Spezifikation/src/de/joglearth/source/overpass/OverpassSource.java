package de.joglearth.source.overpass;

import java.util.Collection;

import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.surface.Location;


/**
 * Provides responses from the OverpassAPI, for e.g. detailed information to a POI or a place.
 */
public class OverpassSource implements Source<OverpassQuery, Collection<Location>> {

    @Override
    public SourceResponse<Collection<Location>> requestObject(OverpassQuery key,
            SourceListener<OverpassQuery, Collection<Location>> sender) {
        return null;
    }
}
