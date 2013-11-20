package de.joglearth.source;

/**
 * The interface SourceListener offers methods to get asynchronous requests of a source.
 * 
 * @param <Key> identifier for the objects
 * @param <Value>
 */
public interface SourceListener<Key, Value> {

    /**
     * Asynchronous request of a source. The source must load the data from the internet.
     * 
     * @param key identifier for the objects
     * @param v Value of the response
     */
    void requestCompleted(Key key, Value v);
}
