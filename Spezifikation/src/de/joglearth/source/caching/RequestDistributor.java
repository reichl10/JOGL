package de.joglearth.source.caching;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.util.Predicate;


/**
 * Dispatches requests for objects to a number of caches and an underlying source.
 * 
 * Retrieved objects are moved to faster caches after access, thus optimizing the response time for
 * repeating queries.
 */
public class RequestDistributor<Key, Value> implements Source<Key, Value> {

    private int[]                  FreeCacheSpace;
    private ArrayList<CacheHandle> caches;
    private HashSet<Key>           pendingRequests; // prevent processing multiple requests for same
                                                    // key
    private Source<Key, Value>     source;
    private ObjectMeasure<Value>   measure;


    /**
     * Organizes following meta-data of entries: size of entry, identifier of entry (key), measure
     * for time passed since last use Returns hashcode of entry determined by key.
     * 
     */
    private class CacheEntry {

        public int size;
        public Key key;
        public int lastUsed;


        @Override
        public int hashCode() {
            return key.hashCode();
        }
    }

    private class CacheHandle {

        public Cache<Key, Value>   cache;
        public TreeSet<CacheEntry> tree;
        public HashSet<CacheEntry> hash;
        public int                 maxSize;
    }

    private class CacheListener implements SourceListener<Key, Value> {

        @Override
        public void requestCompleted(Key key, Value value) {
            // TODO Automatisch generierter Methodenstub

        }
    }


    /**
     * Appends a new cache, which will have the lowest priority of any cache added so far.
     * 
     * @param cache The cache
     * @param maxSize The maximum size in units defined by the ObjectMeasure passed in the
     *        constructor, has to be greater than <code>0</code>
     */
    public void addCache(Cache<Key, Value> cache, int maxSize) {
        // TODO maxSize should probably have a minimum value

    }

    /**
     * Changes the maximum size of an existing cache, possibly dropping cached objects in the
     * process.
     * 
     * @param cache The cache to modify
     * @param maxSize The new maximum size, has to be greater than <code>0</code>
     */
    public void setCacheSize(Cache<Key, Value> cache, int maxSize) {
        // TODO maxSize should probably have a minimum value

    }

    /**
     * Replaces the {@link de.joglearth.source.Source}.
     * 
     * @param source The new source
     */
    public void setSource(Source<Key, Value> source) {
        this.source = source;
    }

    /**
     * Tries to retrieve a requested object. Asks {@link de.joglearth.source.caching.Cache} in the
     * order of their hierarchy, if the object is not in the <code>Caches</code> it asks its
     * {@link de.joglearth.source.Source}. Objects that are not found in the primary
     * <code>Cache</code>, but in another <code>Cache</code> or the <code>Source</code> are stored
     * in the primary <code>Cache</code>. This may lead to displacements of other objects from the
     * primary <code>Cache</code>.
     * 
     * @param key Identifier of the requested object
     * @param sender Entity to be notified when request is completed asynchronous
     */
    @Override
    public SourceResponse<Value> requestObject(Key key,
            SourceListener<Key, Value> sender) {
        for (CacheHandle c : caches) {
            // Abklappern
        }
        return null;
    }

    /**
     * When cache exists, drop last used entry from cache and add it to next lower cache if cache
     * exists. Method calls itself recursive if next lower cache is full. When argument is invalid,
     * this method does nothing.
     * 
     * @param ArrayPositionOfCacheToDropEntry The position of a cache in the <code>caches</code>
     *        Array
     */
    private void displace(int ArrayPositionOfCacheToDropEntry) {}

    /**
     * Erases Key from cache1 and puts it in cache2. Updates <code>tree</code>, hashLastUsed and
     * freeCacheSpace of both caches. If cache2 is null or invalid, method will return true and the
     * entry will be lost. If Key or cache1 aren't valid, method will do nothing but return true.
     * Method will do nothing and return false when cache2 is full.
     * 
     * @param a Key of entry to be switched to other cache
     * @param cache1 Cache where entry is stored and shall be dropped
     * @param cache2 Cache where entry shall be placed
     * 
     */
    private boolean switchCache(Key a, int cache1, int cache2) {
        return false;
    }

    /**
     * Constructor.
     * 
     * @param m The <code>ObjectMeasure</code> to use
     */
    public RequestDistributor(ObjectMeasure<Value> m) {
        /*
         * this.tree = new TreeSet<CacheEntry>(new Comparator<CacheEntry>() { public int
         * compare(CacheEntry a, CacheEntry b) { //a.lastUsed < b.lastUsed return } });
         */
        measure = m;
    }

    /**
     * Default constructor. Uses {@link de.joglearth.source.caching.UnityMeasure} as
     * {@link de.joglearth.source.caching.ObjectMeasure}.
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
     * Removes all objects from the {@link de.joglearth.source.caching.Cache} that fulfill the
     * {@link de.joglearth.util.Predicate}.
     * 
     * @param pred Conformance with that <code>Predicate</code> leads to deletion of
     *        that object
     */
    public void dropAll(Predicate<Key> pred) {

    }

    /**
     * Removes an object stored under a <code>Key</code> from the
     * {@link de.joglearth.source.caching.Cache} that contains it.
     * 
     * @param k The key identifying the object
     */
    public void dropObject(Key k) {

    }

}
