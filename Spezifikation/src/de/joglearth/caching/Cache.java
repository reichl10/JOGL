package de.joglearth.caching;

import de.joglearth.source.Source;


/**
 * Stores objects of type <Value> with an identifier <Key>
 * and returns them on request according to their ID.
 * 
 */
public interface Cache<Key, Value>
extends Source<Key, Value> {
	

        /**
         * Stores object if type is valid and key isn't used yet.
         * 
         * @param k ID of object
         * @param v object to be stored
         */
	void putObject(Key k, Value v);
	
	/**
         * Erases reference to object if key is valid ID of stored object.
         * 
         * @param k ID of object to be dropped
         */
        void dropObject(Key k);

	/**
         * Returns all stored objects. 
         * Main purpose of this method is to inform classes about content when
         * get new reference to cache that already exists and isn't empty anymore.
         */
	Iterable<Key> getExistingObjects();
}

