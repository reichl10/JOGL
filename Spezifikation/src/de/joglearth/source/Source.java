package de.joglearth.source;

/**
 * Offers methods to get objects identified by a specific key; gets the results in a synchronous or
 * an asynchronous way.
 * 
 * @param Key Identifier for the objects
 * @param Value The type of value retrieved by the <code>Source</code>
 */
public interface Source<Key, Value> {

    /**
     * Tries to load and return an object if it is available locally. Otherwise it is attempted to load
     * the object in an asynchronous way.
     * 
     * @param key Identifier for the objects. Must not be null
     * @param sender Receiver of the response. May be null
     * @return A <code>SourceResponse</code> that contains either <code>ASYNCHRONOUS</code> or
     *         <code>SYNCHRONOUS</code> or <code>MISSING</code>
     */
    SourceResponse<Value> requestObject(Key key, SourceListener<Key, Value> sender);
    
    /**
     * TODO
     */
    void dispose();
}
