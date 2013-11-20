package de.joglearth.source;

/**
 * The class SourceResponse gets the responses of a source.
 * 
 * @param <Value>
 */
public class SourceResponse<Value> {

    SourceResponseType response;
    Value              value;


    /**
     * Constructor. Initializes the {@link SourceResponse}.
     * 
     * @param r Type of the response of a source. SYNCHRONOUS: Shows if the response includes the
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
