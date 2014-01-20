package de.joglearth.source.caching;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
        // TODO
        // System.out.println("Added Cache "+cache.getClass().getName()+"  with Size: "+maxSize);
        if (maxSize < 1) {
            throw new IllegalArgumentException("Cache size should be > 0");
        }
        caches.add(cache);
        cacheSizeMap.put(cache, new Integer(maxSize));
        Map<Key, BigInteger> lastUsedHashMap = new HashMap<Key, BigInteger>();
        lastUsedMap.put(cache, lastUsedHashMap);
        Iterable<Key> cachedObjectKeysIterable = cache.getExistingObjects();
        Integer sizeOfObjectsInCache = 0;
        for (Key key : cachedObjectKeysIterable) {
            CacheMoveListener listener = new CacheMoveListener();
            SourceResponse<Value> response = cache.requestObject(key, listener);
            if (response.response == SourceResponseType.SYNCHRONOUS) {
                sizeOfObjectsInCache += measure.getSize(response.value);
                lastUsedHashMap.put(key, getNextStamp());
            } else if (response.response == SourceResponseType.ASYNCHRONOUS) {
                synchronized (this) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                sizeOfObjectsInCache += measure.getSize(listener.value);
                lastUsedHashMap.put(key, getNextStamp());
            }
        }
        usedSizeMap.put(cache, sizeOfObjectsInCache);
        if (sizeOfObjectsInCache > maxSize) {
            makeSpaceInCache(cache, sizeOfObjectsInCache - maxSize);
        }
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
        if (caches.contains(cache)) {
            Integer preVal = cacheSizeMap.put(cache, new Integer(maxSize));
            int pre = preVal.intValue();
            if (maxSize < pre) {
                makeSpaceInCache(cache, pre - maxSize);
            }
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
        if (isAllreadyWaiting(key)) {
            addRequestListener(key, sender);
            return new SourceResponse<Value>(SourceResponseType.ASYNCHRONOUS, null);
        }
        SourceResponse<Value> cacheResponse = askCaches(0, key, sender);
        if (cacheResponse.response != SourceResponseType.MISSING) {
            if (cacheResponse.response == SourceResponseType.ASYNCHRONOUS)
                addRequestListener(key, sender);
            return cacheResponse;
        } else {
            if (source == null) {
                return new SourceResponse<Value>(SourceResponseType.MISSING, null);
            }
            SourceResponse<Value> response = askSource(key, sender);
            switch (response.response) {
                case MISSING:
                    return response;
                case ASYNCHRONOUS:
                    addRequestListener(key, sender);
                case SYNCHRONOUS: // it is intended to fall-through here
                    return response;
                default:
                    return new SourceResponse<Value>(SourceResponseType.MISSING, null);
            }
        }
    }

    private SourceResponse<Value> askSource(Key k, SourceListener<Key, Value> listener) {
        if (source == null) {
            return new SourceResponse<Value>(SourceResponseType.MISSING, null);
        }
        SourceResponse<Value> response = source.requestObject(k, new SourceAsker(this));
        if (response.response == SourceResponseType.SYNCHRONOUS) {
            requestCompleted(k, response.value);
        } else if (response.response == SourceResponseType.ASYNCHRONOUS) {
            addRequestListener(k, listener);
        }
        return response;
    }

    private SourceResponse<Value> askCaches(int index, Key key, SourceListener<Key, Value> listener) {
        Cache<Key, Value> cache;
        if (caches.size() <= index) {
            return new SourceResponse<Value>(SourceResponseType.MISSING, null);
        }
        cache = caches.get(index);
        SourceResponse<Value> response = cache.requestObject(key, new ObjectRequestListener(
                caches, index, source, this));
        switch (response.response) {
            case MISSING:
                return askCaches(index + 1, key, listener);
            case ASYNCHRONOUS:
                addRequestListener(key, listener);
                return response;
            case SYNCHRONOUS:
                cacheRequestCompleted(cache, key, response.value);
                return response;
            default:
                //TODO System.err.println("Sth. is to wrong here, enum has wrong type.");
                return new SourceResponse<Value>(SourceResponseType.MISSING, null);
        }
    }

    /**
     * Returns True if this request was already registed.
     * 
     * @param k
     * @param s
     */
    private synchronized void addRequestListener(Key k, SourceListener<Key, Value> s) {
        Set<SourceListener<Key, Value>> set = waitingRequestsMap.get(k);
        if (set == null) {
            set = new HashSet<SourceListener<Key, Value>>();
            waitingRequestsMap.put(k, set);
        }
        set.add(s);
    }

    private synchronized boolean isAllreadyWaiting(Key k) {
        Set<SourceListener<Key, Value>> set = waitingRequestsMap.get(k);
        if (set == null)
            return false;
        if (set.size() <= 0)
            return false;
        return true;

    }

    private synchronized void updateLastUsed(Cache<Key, Value> cache, Key k) {
        Map<Key, BigInteger> map = lastUsedMap.get(cache);
        map.put(k, getNextStamp());
    }

    private synchronized BigInteger getNextStamp() {
        lastStamp = lastStamp.add(new BigInteger("1"));
        return lastStamp;
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
    public synchronized void dropAll() {
        for (Cache<Key, Value> cache : caches) {
            cache.dropAll();
            lastUsedMap.get(cache).clear();
            usedSizeMap.put(cache, new Integer(0));
        }
    }

    /**
     * Removes all objects from the {@link de.joglearth.source.caching.Cache} that fulfill the
     * {@link de.joglearth.util.Predicate}.
     * 
     * @param pred Conformance with that <code>Predicate</code> leads to deletion of that object
     */
    public synchronized void dropAll(Predicate<Key> pred) {
        for (Cache<Key, Value> cache : caches) {
            Iterable<Key> keys = lastUsedMap.get(cache).keySet();
            for (Key k : keys) {
                if (pred.test(k)) {
                    dropObjectFromCache(cache, k);
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
    public synchronized void dropObject(Key k) {
        for (Cache<Key, Value> c : caches) {
            if (lastUsedMap.get(c).containsKey(k)) {
                dropObjectFromCache(c, k);
            }
        }
    }
    
    private void dropObjectFromCache(Cache<Key, Value> c, Key k) {
        CacheMoveListener listener = new CacheMoveListener();
        SourceResponse<Value> response = c.requestObject(k, listener);
        Value value = null;
        if (response.response == SourceResponseType.ASYNCHRONOUS) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            value = listener.value;
        } else if (response.response == SourceResponseType.SYNCHRONOUS) {
            value = response.value;
        } else {
            //TODO System.err.println("Cache didn't have a object, should not happen!");
            return;
        }
        removeFromCache(c, k, value);
    }

    private void requestCompleted(Key k, Value v) {
        if (v != null) {
            addToCaches(k, v);
            //TODO System.out.println("Finished adding to caches!");
        }

        Set<SourceListener<Key, Value>> listeners = waitingRequestsMap.remove(k);
        if (listeners != null) {
          //TODO System.out.println("RequestDistributor: Starting calling listeners!");
            for (SourceListener<Key, Value> listener : listeners) {
              //TODO System.out.println("RequestDistributor: Calling listener: "+listener.getClass().getCanonicalName());
                listener.requestCompleted(k, v);
              //TODO System.out.println("RequestDistributor: Finished Calling listener: "+listener.getClass().getCanonicalName());
            }
          //TODO System.out.println("RequestDistributor: Calling Listeners");
        }
      //TODO System.out.println("RequestDistributor: Finished requestCompleted");
    }

    private synchronized void cacheRequestCompleted(Cache<Key, Value> c, Key k, Value v) {
        if (caches.size() > 0) {
            int index = caches.indexOf(c);
            if (index != -1 && index != 0) {
                removeFromCache(c, k, v);
                addToCaches(k, v);
            }
        }
        Set<SourceListener<Key, Value>> listeners = waitingRequestsMap.remove(k);
        if (listeners != null) {
            for (SourceListener<Key, Value> listener : listeners) {
                listener.requestCompleted(k, v);
            }
        }
    }

    private synchronized void removeFromCache(Cache<Key, Value> c, Key k, Value v) {
        Integer size = measure.getSize(v);
        removeUsedSpace(c, size);
        c.dropObject(k);
        lastUsedMap.get(c).remove(k);
    }

    private void addToCaches(Key k, Value v) {
        if (caches.size() < 1)
            return;
        Cache<Key, Value> topCache = caches.get(0);
        Integer sizeOfCache = cacheSizeMap.get(topCache);
        if (sizeOfCache < measure.getSize(v)) {
            throw new RuntimeException("The Object is bigger then the Level1 Cache");
        }
        addToCache(0, k, v);
    }

    private void addToCache(int index, Key k, Value v) {
        if (index >= caches.size()) {
            throw new IllegalArgumentException();
        }
        Cache<Key, Value> cache = caches.get(index);
        Integer freeSpace = getFreeSpaceInCache(cache);
        Integer sizeOfValue = measure.getSize(v);
        if (sizeOfValue > freeSpace) {
            Integer cacheSize = cacheSizeMap.get(cache);
            Integer spaceToMake = sizeOfValue * 5;
            if (spaceToMake > cacheSize)
                spaceToMake = cacheSize;
            makeSpaceInCache(index, spaceToMake);
        }
        cache.putObject(k, v);
        addUsedSpace(cache, sizeOfValue);
        updateLastUsed(cache, k);
    }

    private Integer getFreeSpaceInCache(Cache<Key, Value> cache) {
        Integer sizeOfCache = cacheSizeMap.get(cache);
        Integer usedSizeOfCache = usedSizeMap.get(cache);
        //if (sizeOfCache == null)
            //TODO System.err.println("SizeOfCache is null");
        //if (usedSizeOfCache == null)
            //TODO System.err.println("UsedSizeOfCache is null");
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

    private synchronized void makeSpaceInCache(int index, Integer space) {
        if (caches.size() <= index || space == 0)
            return;
        Cache<Key, Value> cache = caches.get(index);
        Integer cacheSize = cacheSizeMap.get(cache);
        Integer spaceUsed = usedSizeMap.get(cache);
        //TODO System.out.println("This Cache has "+cacheSize+" space, "+spaceUsed+" is used! We want: "+space);
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
        boolean hasNextCache = caches.size() > index + 1;
        //TODO System.out.println("Space we want: "+space);
        while (spaceMade < space && list.size() > 0) {
            //TODO System.out.println("Remove One from " + list.size());
            
            Entry<Key, BigInteger> entry = list.pop();
            CacheMoveListener listener = new CacheMoveListener();
            SourceResponse<Value> response = cache.requestObject(entry.getKey(), listener);
            if (response.response == SourceResponseType.SYNCHRONOUS) {
                if (hasNextCache) {
                    CacheEntry cEntry = new CacheEntry(entry.getKey(), response.value,
                            lastUsed.remove(entry.getKey()));
                    removedSet.add(cEntry);
                }
                Integer sizeOfRemovedEntry = measure.getSize(response.value);
                //TODO System.out.println("Removed Entry was worth: "+sizeOfRemovedEntry);
                cache.dropObject(entry.getKey());
                spaceMade += sizeOfRemovedEntry;
            } else if (response.response == SourceResponseType.ASYNCHRONOUS) {
                try {
                    //TODO System.out.println("Waiting for Async answer!");
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (hasNextCache) {
                    CacheEntry cEntry = new CacheEntry(listener.key, listener.value,
                            lastUsed.remove(listener.key));
                    removedSet.add(cEntry);
                }
                if (listener.value != null) {
                    Integer sizeOfRemovedEntry = measure.getSize(listener.value);
                    // TODO System.out.println("Removed Entry was worth: "+sizeOfRemovedEntry);
                    spaceMade += sizeOfRemovedEntry;
                }
                cache.dropObject(listener.key);

            }
            //TODO System.out.println("We allready made:"+spaceMade);
        }
        removeUsedSpace(cache, spaceMade);
        if (hasNextCache) {
            Cache<Key, Value> s2Cache = caches.get(index + 1);
            Integer usedSpaceL2 = usedSizeMap.get(s2Cache);
            Integer sizeL2 = cacheSizeMap.get(s2Cache);
            if (sizeL2-usedSpaceL2 < spaceMade)
                makeSpaceInCache(index + 1, spaceMade-(sizeL2-usedSpaceL2));
            for (CacheEntry ce : removedSet) {
                addToCache(index + 1, ce.key, ce.value);
                Map<Key, BigInteger> usedMap = lastUsedMap.get(s2Cache);
                usedMap.put(ce.key, ce.lastUsed);
            }
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

          //TODO System.err.println("RequestDistributor: async request completed "
           //TODO          + (value == null ? "(null) " : "") + "from cache for " + key);
          //TODO   System.out.println("RequestDistributor: Waiting for Sync");
            synchronized (RequestDistributor.this) {
              //TODO System.out.println("RequestDistributor: Entering Sync");
                if (value == null) {
                  //TODO System.out.println("Got No Value");
                    if (caches.size() > cIndex + 1) {
                        Cache<Key, Value> nextCache = _caches.get(cIndex + 1);
                        ObjectRequestListener listener = new ObjectRequestListener(_caches,
                                cIndex + 1,
                                _source, _rd);
                        SourceResponse<Value> response = nextCache.requestObject(key,
                                listener);
                        switch (response.response) {
                            case MISSING:
                                listener.requestCompleted(key, null);
                                break;
                            case SYNCHRONOUS:
                                _rd.cacheRequestCompleted(nextCache, key, response.value);
                                break;
                            case ASYNCHRONOUS:
                                break;
                            default:
                                break;
                        }
                    } else {
                        if (_source != null) {
                            SourceResponse<Value> response = _source.requestObject(key,
                                    new SourceAsker(_rd));
                            switch (response.response) {
                                case MISSING:
                                    _rd.requestCompleted(key, null);
                                    break;
                                case SYNCHRONOUS:
                                    _rd.requestCompleted(key, response.value);
                                    break;
                                case ASYNCHRONOUS:
                                    break;
                                default:
                                    break;
                            }
                        } else {
                            _rd.requestCompleted(key, null);
                        }
                    }
                } else {
                  //TODO System.out.println("RequestDistributor: Got a Value");
                    _rd.requestCompleted(key, value);
                  //TODO System.out.println("RequestDistributor: Delivered Value");
                }
            }
          //TODO System.out.println("RequestDistributor: Exiting Sync");
        }

    }

    private class SourceAsker implements SourceListener<Key, Value> {

        RequestDistributor<Key, Value> rd;


        public SourceAsker(RequestDistributor<Key, Value> r) {
            rd = r;
        }

        @Override
        public void requestCompleted(Key key, Value value) {
            //TODO System.err.println("RequestDistributor: async request completed "
                //    + (value == null ? "(null) " : "") + "from source for " + key);
            rd.requestCompleted(key, value);
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

    private class CacheMoveListener implements SourceListener<Key, Value> {
        public volatile Key key;
        public volatile Value value;


        public CacheMoveListener() {
        }

        @Override
        public void requestCompleted(Key key, Value value) {
            this.key = key;
            this.value = value;
            synchronized (RequestDistributor.this) {
                RequestDistributor.this.notify();
            }
        }

    }

    @Override
    public void dispose() {
        // TODO: Block incomming changes
        // move to filesystemcache if last cache is one!
        if (caches.size() > 1) {
            Cache<Key, Value> lastCache = caches.get(caches.size()-1);
            if (lastCache instanceof FileSystemCache) {
                Integer sizeOfFilesystem = cacheSizeMap.get(lastCache);
                Integer sizeOfMovedEntrys = 0;
                for (int p = 0; p < (caches.size()-1); p++) {
                    Cache<Key, Value> cacheToMove = caches.get(p);
                    Integer size = usedSizeMap.get(cacheToMove);
                    Integer sizeToMoveFromThisCache = size > sizeOfFilesystem ? sizeOfFilesystem : size;
                    if (sizeToMoveFromThisCache > sizeOfFilesystem-sizeOfMovedEntrys) {
                        sizeToMoveFromThisCache = sizeOfFilesystem-sizeOfMovedEntrys;
                    }
                    Integer sizeOfRemovedFromThisCache = 0;
                    makeSpaceInCache(lastCache, sizeToMoveFromThisCache);
                    Map<Key, BigInteger> lastUsed = lastUsedMap.get(cacheToMove);
                    Set<Entry<Key, BigInteger>> entrySet = lastUsed.entrySet();
                    LinkedList<Entry<Key, BigInteger>> list = new LinkedList<Entry<Key, BigInteger>>(entrySet);
                    Collections.sort(list, new Comparator<Entry<Key, BigInteger>>() {

                        @Override
                        public int compare(Entry<Key, BigInteger> o1, Entry<Key, BigInteger> o2) {
                            return o1.getValue().compareTo(o2.getValue());
                        }

                    });
                    while(sizeToMoveFromThisCache > sizeOfRemovedFromThisCache) {
                        Entry<Key, BigInteger> entry = list.pop();
                        CacheMoveListener listener = new CacheMoveListener();
                        SourceResponse<Value> response = cacheToMove.requestObject(entry.getKey(), listener);
                        if (response.response == SourceResponseType.SYNCHRONOUS) {
                            lastCache.putObject(entry.getKey(), response.value);
                            Integer sizeOfRemovedEntry = measure.getSize(response.value);
                            cacheToMove.dropObject(entry.getKey());
                            sizeOfRemovedFromThisCache += sizeOfRemovedEntry;
                        } else if (response.response == SourceResponseType.ASYNCHRONOUS) {
                            try {
                                //TODO System.out.println("Waiting for Async answer!");
                                wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            lastCache.putObject(entry.getKey(), listener.value);
                            Integer sizeOfRemovedEntry = measure.getSize(listener.value);
                            cacheToMove.dropObject(entry.getKey());
                            sizeOfRemovedFromThisCache += sizeOfRemovedEntry;
                        }
                    }
                    sizeOfMovedEntrys += sizeOfRemovedFromThisCache;
                }
            }
        }
        source.dispose();
        for (Cache<Key, Value> c : caches) {
            c.dispose();
        }
        caches.clear();
    }
}
