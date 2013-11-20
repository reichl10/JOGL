package de.joglearth.source;

/**
 * The class SourceResponse gets the responses of a source.
 * 
 * @param <Value>
 * @uml.annotations uml_dependency=
 *                  "mmi:///#jsrctype^name=SourceResponseType[jcu^name=SourceResponseType.java[jpack^name=de.joglearth.source[jsrcroot^srcfolder=src[project^id=Entwurf]]]]$uml.Enumeration"
 */
public class SourceResponse<Value> {

    SourceResponseType response;
    Value              value;


    /**
     * Constructor SourceResponse.
     * 
     * @param r Type of the response of the source. SYNCHRONOUS: Shows if the response includes the
     *        requested value. ASYNCHRONOUS: The requested value is temporarily not available. It
     *        must be loaded via internet. MISSING: The requested value is not available. It could
     *        not be loaded via internet.
     * @param v Value of the response. Only necessary, when the type of the response is SYNCHRONOUS.
     */
    public SourceResponse(SourceResponseType r, Value v) {
        response = r;
        value = v;
    }
}
