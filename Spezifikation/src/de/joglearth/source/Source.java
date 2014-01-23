package de.joglearth.source;

/**
 * Offers methods to get objects identified by a specific key; gets the results in a synchronous or
 * an asynchronous way.
 * 
 * @param Key Identifier for the objects
 * @param Value The type of value retrieved by the {@link Source}
 */
public interface Source<Key, Value> {

    /**
     * Tries to load and return an object if it is available locally. Otherwise it is attempted to
     * load the object in an asynchronous way.
     * 
     * @param key Identifier for the objects. Must not be null
     * @param sender Receiver of the response. May be null
     * @return A {@link SourceResponse} that contains either {@link SourceResponseType#ASYNCHRONOUS}
     *         or {@link SourceResponseType#SYNCHRONOUS} or {@link SourceResponseType#MISSING}
     */
    SourceResponse<Value> requestObject(Key key, SourceListener<Key, Value> sender);
    

    /**
     *  Called if the program has been closed to terminate all pending processes.
     */
    void dispose();
}
