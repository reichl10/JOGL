package de.joglearth.source.caching;

import java.util.HashMap;

import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.SourceResponseType;

/**
 * Implements the {@link de.joglearth.source.caching.Cache} interface in such way that it stores objects in the memory.
 * 
 */
public class MemoryCache<Key, Value> implements Cache<Key, Value> {

    private HashMap<Key, Value> storage = new HashMap<Key, Value>();


    @Override
    public SourceResponse<Value> requestObject(Key key,
            SourceListener<Key, Value> sender) {
        Value object = storage.get(key);
        SourceResponseType responseType = object != null
                ? SourceResponseType.SYNCHRONOUS : SourceResponseType.MISSING;
        return new SourceResponse<Value>(responseType, object);
    }

    @Override
    public void putObject(Key key, Value value) {
        storage.put(key, value);
    }

    @Override
    public void dropObject(Key key) {
        storage.remove(key);
    }

    @Override
    public Iterable<Key> getExistingObjects() {
        return storage.keySet();
    }

    @Override
    public void dropAll() {
        storage.clear();
    }
}