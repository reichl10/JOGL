package de.joglearth.source;

/**
 * Offers methods to get asynchronous requests of a source.
 * 
 * @param Key Identifier for the objects
 * @param Value The type of value retrieved by the {@link Source}
 */
public interface SourceListener<Key, Value> {

    /**
     * Asynchronous request of a source to the web.
     * 
     * @param key Identifier for the objects
     * @param value The type of value retrieved by the {@link Source}
     */
    void requestCompleted(Key key, Value value);
}
