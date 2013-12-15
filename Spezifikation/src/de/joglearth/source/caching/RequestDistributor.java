package de.joglearth.source.caching;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.SourceResponseType;
import de.joglearth.util.Predicate;


/**
 * Dispatches requests for objects to a number of caches and an underlying source.
 * 
 * Retrieved objects are moved to faster caches after access, thus optimizing the response time for
 * repeating queries.
 */
public class RequestDistributor<Key, Value> implements Source<Key, Value> {

    /*
     * TODO Warum ist CacheHandle weg? Listen für jedes einzelne Attribut wirkt etwas
     * spaghettiesque. Das Design hatte schon einen Sinn so wie es war. ----- Ist das so? Ich komme
     * mit dem was ich da gemacht habe ganz gut zu recht. Und der einfall mit dem TreeSet würde so
     * wie wir uns das vorgestellt hätten nicht funktionieren. Da man vor jedem Löschen aus dem
     * Cache das TreeSet neu aufbauen müsste oder sogar bei jedem Zugrif auf ein Element. (Je
     * nachdem wann es einem Lieber ist die Zeit aufzuwedenen)
     */

    /**
     * Holds my caches.
     */
    private List<Cache<Key, Value>> caches;
    private Map<Cache<Key, Value>, Integer> cacheSizeMap;
    /**
     * Holds the source.
     */
    private Source<Key, Value> source;
    private ObjectMeasure<Value> measure;
    private Map<Key, Set<SourceListener<Key, Value>>> waitingRequestsMap;
    private Map<Cache<Key, Value>, Integer> sizeMap;
    private Map<Cache<Key, Value>, Integer> usedSizeMap;
    private Map<Cache<Key, Value>, Map<Key, BigInteger>> lastUsedMap;
    private BigInteger lastStamp;


    /**
     * Appends a new cache, which will have the lowest priority of any cache added so far.
     * 
     * @param cache The cache
     * @param maxSize The maximum size in units defined by the ObjectMeasure passed in the
     *        constructor, has to be greater than <code>0</code>
     */
    public void addCache(Cache<Key, Value> cache, int maxSize) {
        if (cache == null)
            return;
        if (maxSize < 1) {
            throw new IllegalArgumentException("Cache size should be > 0");
        }
        caches.add(cache);
        cacheSizeMap.put(cache, new Integer(maxSize));
        // TODO: Create lastUsedMap
    }

    /**
     * Changes the maximum size of an existing cache, possibly dropping cached objects in the
     * process.
     * 
     * @param cache The cache to modify
     * @param maxSize The new maximum size, has to be greater than <code>0</code>
     */
    public void setCacheSize(Cache<Key, Value> cache, int maxSize) {
        if (cache == null)
            return;
        if (maxSize < 1) {
            throw new IllegalArgumentException("Cache size should be > 0");
        }
        Integer preVal = cacheSizeMap.put(cache, new Integer(maxSize));
        int pre = preVal.intValue();
        if (maxSize < pre) {
            makeSpaceInCache(cache, pre - maxSize);
        }
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
    public synchronized SourceResponse<Value> requestObject(Key key,
            final SourceListener<Key, Value> sender) {
        Cache<Key, Value> cache;
        try {
            cache = caches.get(0);
        } catch (IndexOutOfBoundsException e) {
            if (source == null)
                return new SourceResponse<Value>(SourceResponseType.MISSING, null);
            if (!addRegisterRequest(key, sender)) {
                return askSource(key, sender);
            } else {
                return new SourceResponse<Value>(SourceResponseType.ASYNCHRONOUS, null);
            }
        }
        SourceResponse<Value> response = askCaches(0, key, sender);
        return response;
    }

    private SourceResponse<Value> askSource(Key k, SourceListener<Key, Value> listener) {
        SourceResponse<Value> response = source.requestObject(k, new SourceAsker(this));
        if (response.response == SourceResponseType.SYNCHRONOUS
                || response.response == SourceResponseType.MISSING) {
            removeRegistredRequest(k);
            if (response.response == SourceResponseType.SYNCHRONOUS) {
                requestCompleted(k, response.value);
            }
        }
        return response;
    }

    private SourceResponse<Value> askCaches(int index, Key key, SourceListener<Key, Value> listener) {
        Cache<Key, Value> cache;
        try {
            cache = caches.get(index);
        } catch (IndexOutOfBoundsException e) {
            return new SourceResponse<Value>(SourceResponseType.MISSING, null);
        }
        if (!addRegisterRequest(key, listener)) {
            SourceResponse<Value> response = cache.requestObject(key, new ObjectRequestListener(
                    caches, 0, source, this));
            if (response.response == SourceResponseType.SYNCHRONOUS) {
                // update lastUsedTime
                addToCaches(key, response.value);
                Map<Key, BigInteger> usedMap = lastUsedMap.get(caches.get(0));
                BigInteger newStamp = lastStamp.add(new BigInteger("1"));
                lastStamp = newStamp;
                usedMap.put(key, newStamp);
            } else if (response.response == SourceResponseType.MISSING) {
                return askCaches(index + 1, key, listener);
            }
            return response;
        } else {
            return new SourceResponse<Value>(SourceResponseType.ASYNCHRONOUS, null);
        }
    }

    private boolean doesRequestExist(Key k) {
        Set<SourceListener<Key, Value>> set = waitingRequestsMap.get(k);
        if (set != null)
            return true;
        return false;
    }

    /**
     * Returns True if this request was allready registed.
     * 
     * @param k
     * @param s
     * @return
     */
    private boolean addRegisterRequest(Key k, SourceListener<Key, Value> s) {
        boolean ret = true;
        Set<SourceListener<Key, Value>> set = waitingRequestsMap.get(k);
        if (set == null) {
            set = new HashSet<SourceListener<Key, Value>>();
            ret = false;
            waitingRequestsMap.put(k, set);
        }
        set.add(s);
        return ret;
    }

    private void removeRegistredRequest(Key k) {
        waitingRequestsMap.remove(k);
    }

    /**
     * Constructor.
     * 
     * @param m The <code>ObjectMeasure</code> to use
     */
    public RequestDistributor(ObjectMeasure<Value> m) {
        measure = m;
        caches = Collections.synchronizedList(new ArrayList<Cache<Key, Value>>(2));
        cacheSizeMap = new Hashtable<Cache<Key, Value>, Integer>();
        waitingRequestsMap = new Hashtable<Key, Set<SourceListener<Key, Value>>>();
        lastUsedMap = new Hashtable<Cache<Key, Value>, Map<Key, BigInteger>>();
        usedSizeMap = new Hashtable<Cache<Key, Value>, Integer>();
        lastStamp = new BigInteger("0");
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
        for (Cache<Key, Value> cache : caches) {
            cache.dropAll();
        }
    }

    /**
     * Removes all objects from the {@link de.joglearth.source.caching.Cache} that fulfill the
     * {@link de.joglearth.util.Predicate}.
     * 
     * @param pred Conformance with that <code>Predicate</code> leads to deletion of that object
     */
    public void dropAll(Predicate<Key> pred) {
        for (Cache<Key, Value> cache : caches) {
            Iterable<Key> keys = cache.getExistingObjects();
            for (Key k : keys) {
                if (pred.test(k)) {
                    cache.dropObject(k);
                }
            }
        }
    }

    /**
     * Removes an object stored under a <code>Key</code> from the
     * {@link de.joglearth.source.caching.Cache} that contains it.
     * 
     * @param k The key identifying the object
     */
    public void dropObject(Key k) {
        for (Cache<Key, Value> c : caches) {
            c.dropObject(k);
        }
    }

    private void requestCompleted(Key k, Value v) {
        addToCaches(k, v);
        Set<SourceListener<Key, Value>> listeners = waitingRequestsMap.remove(k);
        if (listeners != null) {
            for (SourceListener<Key, Value> listener : listeners) {
                listener.requestCompleted(k, v);
            }
        }
    }

    private void addToCaches(Key k, Value v) {
        if (caches.size() < 1)
            return;
        addToCache(0, k, v);
    }

    private void addToCache(int index, Key k, Value v) {
        Cache<Key, Value> cache;
        try {
            cache = caches.get(index);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return;
        }
        Integer freeSpace = getFreeSpaceInCache(cache);
        Integer sizeOfValue = measure.getSize(v);
        if (sizeOfValue > freeSpace) {
            makeSpaceInCache(index, sizeOfValue * 5);
        }
        cache.putObject(k, v);
        addUsedSpace(cache, sizeOfValue);
    }

    private Integer getFreeSpaceInCache(Cache<Key, Value> cache) {
        Integer sizeOfCache = cacheSizeMap.get(cache);
        Integer usedSizeOfCache = usedSizeMap.get(cache);
        return sizeOfCache - usedSizeOfCache;
    }

    private void addUsedSpace(Cache<Key, Value> cache, Integer space) {
        Integer usedSizeOfCache = usedSizeMap.get(cache);
        usedSizeMap.put(cache, usedSizeOfCache + space);
    }

    private void removeUsedSpace(Cache<Key, Value> cache, Integer space) {
        Integer usedSizeOfCache = usedSizeMap.get(cache);
        usedSizeMap.put(cache, usedSizeOfCache - space);
    }

    private void makeSpaceInCache(Cache<Key, Value> c, int spaceToMake) {
        int index = caches.indexOf(c);
        if (index == -1)
            return;
        makeSpaceInCache(index, spaceToMake);
    }

    private void makeSpaceInCache(int index, Integer space) {
        Cache<Key, Value> cache;
        try {
            cache = caches.get(index);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return;
        }
        Map<Key, BigInteger> lastUsed = lastUsedMap.get(cache);
        Set<Entry<Key, BigInteger>> entrySet = lastUsed.entrySet();
        LinkedList<Entry<Key, BigInteger>> list = new LinkedList<Entry<Key, BigInteger>>(entrySet);
        Collections.sort(list, new Comparator<Entry<Key, BigInteger>>() {

            @Override
            public int compare(Entry<Key, BigInteger> o1, Entry<Key, BigInteger> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }

        });
        int spaceMade = 0;
        Set<CacheEntry> removedSet = new HashSet<CacheEntry>();
        while (spaceMade < space) {
            Entry<Key, BigInteger> entry = list.pop();
            SourceResponse<Value> response = cache.requestObject(entry.getKey(), null);
            if (response.response != SourceResponseType.SYNCHRONOUS) {
                System.err.println("This must be changed or it gets to slow!");
            } else {
                CacheEntry cEntry = new CacheEntry(entry.getKey(), response.value,
                        lastUsed.remove(entry.getKey()));
                Integer sizeOfRemovedEntry = measure.getSize(response.value);
                removedSet.add(cEntry);
                cache.dropObject(entry.getKey());
                spaceMade += sizeOfRemovedEntry;
            }
        }
        removeUsedSpace(cache, spaceMade);
        Cache<Key, Value> s2Cache;
        try {
            s2Cache = caches.get(index + 1);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return;
        }
        makeSpaceInCache(index + 1, spaceMade);
        for (CacheEntry ce : removedSet) {
            addToCache(index + 1, ce.key, ce.value);
            Map<Key, BigInteger> usedMap = lastUsedMap.get(s2Cache);
            usedMap.put(ce.key, ce.lastUsed);
        }
    }


    private class ObjectRequestListener implements SourceListener<Key, Value> {

        List<Cache<Key, Value>> _caches;
        private int cIndex;
        Source<Key, Value> _source;
        RequestDistributor<Key, Value> _rd;


        public ObjectRequestListener(List<Cache<Key, Value>> caches, int currentIndex,
                Source<Key, Value> s, RequestDistributor<Key, Value> r) {
            this._caches = caches;
            this.cIndex = currentIndex;
            this._source = s;
            this._rd = r;
        }

        @Override
        public void requestCompleted(Key key, Value value) {
            synchronized (RequestDistributor.this) {
                if (value == null) {
                    // ask next cache
                    Cache<Key, Value> nextCache = null;
                    try {
                        nextCache = _caches.get(cIndex + 1);
                    } catch (IndexOutOfBoundsException e) {}
                    if (nextCache != null) {
                        nextCache.requestObject(key, new ObjectRequestListener(_caches, cIndex + 1,
                                _source, _rd));
                    } else {
                        if (_source != null) {
                            _source.requestObject(key, new SourceAsker(_rd));
                        } else {
                            _rd.requestCompleted(key, null);
                        }
                    }
                } else {
                    _rd.requestCompleted(key, value);
                }
            }
        }

    }

    private class SourceAsker implements SourceListener<Key, Value> {

        RequestDistributor<Key, Value> rd;


        public SourceAsker(RequestDistributor<Key, Value> r) {
            rd = r;
        }

        @Override
        public void requestCompleted(Key key, Value value) {
            synchronized (RequestDistributor.this) {
                rd.requestCompleted(key, value);
            }
        }

    }

    private class CacheEntry {

        public Key key;
        public Value value;
        public BigInteger lastUsed;


        public CacheEntry(Key k, Value v, BigInteger l) {
            key = k;
            value = v;
            lastUsed = l;
        }
    }
}
