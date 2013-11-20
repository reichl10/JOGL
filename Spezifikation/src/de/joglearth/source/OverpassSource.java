package de.joglearth.source;

import de.joglearth.geometry.Tile;
import de.joglearth.surface.Location;
import de.joglearth.rendering.*;
import de.joglearth.source.*;
import de.joglearth.ui.*;


/**
 * The class OverpassSource provides responses from the OverpassAPI, for e.g. detailed information
 * to a POI or a place. The response will be prepared for the LocationManager. This class uses the
 * HTTPUtils for the search request.
 * 
 */
public class OverpassSource implements Source<OverpassQuery, Location[]> {

    /**
     * 
     */
    @Override
    public SourceResponse<Location[]> requestObject(OverpassQuery key,
            SourceListener<OverpassQuery, Location[]> sender) {
        return null;
    }

}
