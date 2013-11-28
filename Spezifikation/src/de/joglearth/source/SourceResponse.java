package de.joglearth.source;

/**
 * Gets the responses of a {@link Source}.
 * 
 * @param Value The <code>SourceResponseType</code> of the value retrieved by the Source
 */
public class SourceResponse<Value> {

    /**
     * The type of this response.
     */
    public SourceResponseType response;
    /**
     * The value of this response.
     */
    public Value              value;


    /**
     * Constructor. Initializes the {@link SourceResponse}.
     * 
     * @param r <code>SourceResponseType</code> of the response of a source
     * @param v Value of the response (Only necessary, when the <code>SourceResponseType</code> of
     *        the response is <code>SYNCHRONOUS</code>)
     */
    public SourceResponse(SourceResponseType r, Value v) {
        response = r;
        value = v;
    }
}
