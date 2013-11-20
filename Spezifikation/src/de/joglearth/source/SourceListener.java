package de.joglearth.source;

/**
 * The interface SourceListener offers methods to get asynchronous requests of a source.
 * 
 * @param Key identifier for the objects
 * @param Value The type of value retrieved by the {@link Source}
 */
public interface SourceListener<Key, Value> {

    /**
     * Asynchronous request of a source to the web.
     * 
     * @param key identifier for the objects
     * @param v Value of the response
     */
    void requestCompleted(Key key, Value v);
}
