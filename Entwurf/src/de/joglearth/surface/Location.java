package de.joglearth.surface;

import de.joglearth.geometry.GeoCoordinates;


/** wtf
 * @uml.annotations 
 *    uml_dependency="mmi:///#jsrctype^name=LocationType[jcu^name=LocationType.java[jpack^name=de.joglearth.surface
 *    [jsrcroot^srcfolder=src[project^id=Entwurf]]]]$uml.Enumeration"
 */
public class Location implements Cloneable {
	public GeoCoordinates point;
	public LocationType type;
	public String details;

	public Location(GeoCoordinates point, LocationType type, String details) {
		this.point = point;
		this.details = details;
	}
}
