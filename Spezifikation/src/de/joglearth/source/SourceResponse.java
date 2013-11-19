package de.joglearth.source;

/**
 * @uml.annotations 
 *    uml_dependency="mmi:///#jsrctype^name=SourceResponseType[jcu^name=SourceResponseType.java[jpack^name=de.joglearth.source[jsrcroot^srcfolder=src[project^id=Entwurf]]]]$uml.Enumeration"
 */
public class SourceResponse<Value> {
	SourceResponseType response;
	Value value;

	public SourceResponse(SourceResponseType r, Value v) {
		response = r;
		value = v;
	}
}