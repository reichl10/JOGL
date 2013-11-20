package de.joglearth.source;

import de.joglearth.geometry.Tile;


/**
 * Offers methods to get objects identified by a specific key; gets the results in a synchronous or
 * an asynchronous way.
 * 
 * @param Key Identifier for the objects
 * @param Value The type of value retrieved by the Source
 */
public interface Source<Key, Value> {

    /**
     * Tries to load and return an object if it available locally. Otherwise it is attempted
     * to load the object in an asynchronous way and <code>null</code> is returned.
     * @param key
     * @param sender
     * @return
     */
    SourceResponse<Value> requestObject(Key key, SourceListener<Key, Value> sender);
}
