package de.joglearth.source;

/**
 * Gets the responses of a {@link Source}.
 * 
 * @param Value The {@link SourceResponseType} of the value retrieved by the Source
 */
public class SourceResponse<Value> {

    SourceResponseType response;
    Value              value;


    /**
     * Constructor. Initializes the {@link SourceResponse}.
     * 
     * @param r {@link SourceResponseType} of the response of a source
     * @param v Value of the response (Only necessary, when the <code>SourceResponseType</code> of
     *        the response is <code>SYNCHRONOUS</code>)
     */
    public SourceResponse(SourceResponseType r, Value v) {
        response = r;
        value = v;
    }
}
