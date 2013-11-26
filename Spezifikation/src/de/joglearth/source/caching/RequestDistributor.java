package de.joglearth.source.caching;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.util.Predicate;


/**
 * Receives requests for objects, can get the objects from one {@link Source} that can be set.
 * Stores a requested <code>Value</code> in {@link Cache}s under a given <code>Key</code> to
 * minimize loading time.
 * 
 */
public class RequestDistributor<Key, Value> implements Source<Key, Value> {

    private int[]                          FreeCacheSpace;
    private ArrayList<TreeSet<CacheEntry>> tree;
    private ArrayList<HashSet<CacheEntry>> hashLastUsed;

    // prevent processing multiple requests for same key
    private ArrayList<Key>                 pendingRequests;
    private ArrayList<Cache<Key, Value>>   caches;
    private Source<Key, Value>             source;
    private ObjectMeasure<Value>           measure;


    /*
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

    /**
     * Changes the size of a {@link cache} to a given value.
     * 
     * @param cache The <code>Cache</code> whose size should be changed
     * @param maxSize The new size of the <code>Cache</code>
     */
    public void setCacheSize(Cache<Key, Value> cache, int maxSize) {

    }

    /**
     * Replaces the {@link Source}.
     * 
     * @param source The new <code>Source</code>
     */
    public void setSource(Source<Key, Value> source) {
        this.source = source;
    }

    /**
     * Tries to retrieve a requested object. Asks {@link Cache}s in the order of their hierarchy, if
     * the object is not in the <code>Caches</code> it asks its {@link Source}. Objects that are not
     * found in the primary <code>Cache</code>, but in another <code>Cache</code> or the
     * <code>Source</code> are stored in the primary <code>Cache</code>. This may lead to
     * displacements of other objects from the primary <code>Cache</code>.
     * 
     * @param key Identifier of the requested object
     * @param sender Entity to be notified when request is completed asynchronous
     */
    @Override
    public SourceResponse<Value> requestObject(Key key,
            SourceListener<Key, Value> sender) {
        for (Cache<Key, Value> c : caches) {
            
        }
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
     * Constructor. Assigns a value to the {@link ObjectMeasure}.
     * 
     * @param m The <code>Measure</code> that should be used
     */
    public RequestDistributor(ObjectMeasure<Value> m) {
        /*
         * this.tree = new TreeSet<CacheEntry>(new Comparator<CacheEntry>() { public int
         * compare(CacheEntry a, CacheEntry b) { //a.lastUsed < b.lastUsed return } });
         */
        measure = m;
    }

    /**
     * Default constructor. Uses {@link UnityMeasure} as {@link ObjectMeasure}.
     */
    public RequestDistributor() {
        this(new UnityMeasure<Value>());
    }

    /**
     * Drops all cached objects.
     */
    public void dropAll() {

    }

    /**
     * Removes all objects from the {@link cache}s that fulfill the {@link Predicate}.
     * @param pred Conformance with that <code>Predicate</code> leads to deletion of that object
     */
    public void dropAll(Predicate<Key> pred) {

    }

    /**
     * Removes an object stored under a <code>Key</code> from the {@link Cache} that contains it.
     * @param k The <code>Key</code> of the object to drop
     */
    public void dropObject(Key k) {

    }
}