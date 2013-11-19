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
 * @uml.annotations uml_dependency=
 *                  "mmi:///#jsrctype^name=OverpassQuery[jcu^name=OverpassQuery.java[jpack^name=de.joglearth.source[jsrcroot^srcfolder=src[project^id=Entwurf]]]]$uml.Class"
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
