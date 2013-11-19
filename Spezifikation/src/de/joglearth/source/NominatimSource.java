package de.joglearth.source;

import de.joglearth.surface.Location;


/**
 * The class NominatimSource provides responses of search requests, for e.g. search requests for
 * places or detailed information to a point. The response will be prepared for the LocationManager.
 * This class uses the HTTPUtils for the search request.
 * 
 * @uml.annotations uml_dependency=
 *                  "mmi:///#jsrctype^name=NominatimQuery[jcu^name=NominatimQuery.java[jpack^name=de.joglearth.source[jsrcroot^srcfolder=src[project^id=Entwurf]]]]$uml.Class"
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
