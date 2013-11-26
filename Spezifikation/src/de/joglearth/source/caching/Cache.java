package de.joglearth.source.caching;

import de.joglearth.source.Source;


/**
 * Stores objects of type <code>Value</code> with an identifier <code>Key</code> and returns them on
 * request according to their ID.
 * 
 */
public interface Cache<Key, Value>
        extends Source<Key, Value> {

    /**
     * Stores object identified by a <code>Key</code>. An existing object with that <code>Key</code>
     * may be overwritten.
     * 
     * @param k ID of object
     * @param v The object to be stored
     */
    void putObject(Key k, Value v);

    /**
     * Erases the reference to an object if the <code>Key</code> is a valid ID of a stored object.
     * 
     * @param k The ID of object to be dropped
     */
    void dropObject(Key k);

    /**
     * Can be used to get the objects that already exist when the cache is initialized.
     * @return All stored objects
     */
    Iterable<Key> getExistingObjects();

    /**
     * Drops all objects contained in a cache.
     */
    void dropAll();
}
