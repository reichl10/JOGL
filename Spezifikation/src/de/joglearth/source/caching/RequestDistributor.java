package de.joglearth.source.caching;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.util.Predicate;


/**
 * Receives requests for objects, can get the objects from one Source that can be set. Stores
 * requested objects in caches to minimize loading-time.
 * 
 */
public class RequestDistributor<Key, Value> implements Source<Key, Value> {

    private int[]                          FreeCacheSpace;
    private ArrayList<TreeSet<CacheEntry>> tree;
    private ArrayList<HashSet<CacheEntry>> hashLastUsed;
    private ArrayList<Key>                 pendingRequests; // prevent processing multiple requests
                                                            // for same key
    private ArrayList<Cache<Key, Value>>   caches;
    private Source<Key, Value>             source;
    private ObjectMeasure<Value>           measure;


    /**
     * Organizes following meta-data of entries: size of entry, identifier of entry (key), measure
     * for time passed since last use Returns hashcode of entry determined by key.
     */
    private class CacheEntry {

        public int                 size;
        public Key                 key;
        public int                 lastUsed;
        private Source<Key, Value> source;


        @Override
        public int hashCode() {
            return key.hashCode();
        }
    }


    /**
     * Adds new cache and creates new <code>tree</code>, hashLastUsed and FreeCacheSpace.
     */
    public void addCache(Cache<Key, Value> cache, int maxSize) {

    }

    public void setCacheSize(Cache<Key, Value> cache, int maxSize) {

    }

    /**
     * Replace Source.
     */
    public void setSource(Source<Key, Value> source) {
        this.source = source;
    }

    /**
     * Tries to return requested object. Consults caches first and updates hashLastUsed and entry in
     * <code>tree</code> if object found in cache. When found in other cache than primary cache, put
     * object in primary cache. If object not found in caches, consult <code>Source</code>, add to
     * pendingRequests and return null with responseType ASYNCHRONE. If no Source set, return null
     * and responseType MISSING instead.
     * 
     * @param key Key (identifier) of requested object
     * @param sender Entity to be notified when request is completed asynchrone
     */
    @Override
    public SourceResponse<Value> requestObject(Key key,
            SourceListener<Key, Value> sender) {
        for (Cache<Key, Value> c : caches) {
            // Abklappern
        }
        // dann source fragen
        return null;
    }

    /*
     * Adds element <code>response</code> to it's first cache and updates FreeCacheSpace. Calls
     * <code>displace()</code> if cache is already full. Also erases <code>Key</code> of
     * <code>response</code> from <code>pendingRequests</code> and notifies listeners that requested
     * object is available now. Method wont do anything when parameter is null or Key is already
     * represented in cache.
     * 
     * @param response The requested entry to be added to the Cache
     * 
     * 
     * @Override public void requestCompleted(Key k, Value v) { }
     */
    /**
     * When cache exists, drop last used entry from cache and add it to next lower cache if cache
     * exists. Method calls itself recursive if next lower cache is full. When argument is invalid,
     * this method does nothing.
     * 
     * @param ArrayPositionOfCacheToDropEntry
     */
    private void displace(int ArrayPositionOfCacheToDropEntry) {}

    /**
     * Erases Key from cache1 and puts it in cache2. Updates <code>tree</code>, hashLastUsed and
     * freeCacheSpace of both caches. If cache2 is null or invalid, method will return true and the
     * entry will be lost. If Key or cache1 aren't valid, method will do nothing but return true.
     * Method will do nothing and return false when cache2 is full.
     * 
     * @param a Key of entry to be switched to other cache.
     * @param cache1 Cache where entry is stored and shall be dropped.
     * @param cache2 Cache where entry shall be placed.
     * 
     */
    private boolean switchCache(Key a, int cache1, int cache2) {
        return false;
    }

    /**
     * Standard-constructor
     */
    public RequestDistributor(ObjectMeasure<Value> m) {
        /*
         * this.tree = new TreeSet<CacheEntry>(new Comparator<CacheEntry>() { public int
         * compare(CacheEntry a, CacheEntry b) { //a.lastUsed < b.lastUsed return } });
         */
        measure = m;
    }

    public RequestDistributor() {
        this(new UnityMeasure<Value>());
    }

    public void dropAll() {

    }

    public void dropAll(Predicate<Key> pred) {

    }

    public void dropObject(Key k) {

    }

}
