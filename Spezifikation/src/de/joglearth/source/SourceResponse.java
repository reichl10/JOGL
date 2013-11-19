package de.joglearth.source;

/**
 * @param <Value>
 * @uml.annotations uml_dependency=
 *                  "mmi:///#jsrctype^name=SourceResponseType[jcu^name=SourceResponseType.java[jpack^name=de.joglearth.source[jsrcroot^srcfolder=src[project^id=Entwurf]]]]$uml.Enumeration"
 * 
 * 
 * 
 */
public class SourceResponse<Value> {

    SourceResponseType response;
    Value              value;


    /**
     * 
     * @param r Type of the response of the source
     * @param v Value
     */
    public SourceResponse(SourceResponseType r, Value v) {
        response = r;
        value = v;
    }
}
