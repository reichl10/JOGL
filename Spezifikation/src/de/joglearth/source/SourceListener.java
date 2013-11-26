package de.joglearth.source;

/**
 * Offers methods to get asynchronous requests of a {@link Source}.
 * 
 * @param Key Identifier for the objects
 * @param Value The type of value retrieved by the <code>Source</code>
 */
public interface SourceListener<Key, Value> {

    /**
     * Asynchronous request of a {@link source} to the web.
     * 
     * @param key Identifier for the objects
     * @param value The type of value retrieved by the <code>Source</code>
     */
    void requestCompleted(Key key, Value value);
}
