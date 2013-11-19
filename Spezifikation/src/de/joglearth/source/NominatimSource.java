package de.joglearth.source;

import de.joglearth.surface.Location;


/**
 * @uml.annotations 
 *    uml_dependency="mmi:///#jsrctype^name=NominatimQuery[jcu^name=NominatimQuery.java[jpack^name=de.joglearth.source[jsrcroot^srcfolder=src[project^id=Entwurf]]]]$uml.Class"
 */
public class NominatimSource implements Source<NominatimQuery, Location[]> {

	public NominatimSource(SourceListener<NominatimQuery, Location[]> owner) {
	}

	@Override
	public SourceResponse<Location[]> requestObject(NominatimQuery key,
			SourceListener<NominatimQuery, Location[]> sender) {
		// TODO Automatisch erstellter Methoden-Stub
		return null;
	}

}
