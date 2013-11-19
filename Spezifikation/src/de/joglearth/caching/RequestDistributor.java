package de.joglearth.caching;

import java.util.ArrayList;
import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;



public class RequestDistributor<Key, Value> implements Source<Key, Value> {

    private ArrayList<Cache<Key, Value>> caches;
    private Source<Key, Value>           source;


    public void addCache(Cache<Key, Value> cache, int maxSize) {

    }

    public void setSource(Source<Key, Value> source) {
        this.source = source;
                
    }

    @Override
    public SourceResponse<Value> requestObject(Key key,
            SourceListener<Key, Value> sender) {
        for (Cache<Key, Value> c : caches) {
            // Abklappern
        }
        // dann source fragen
        return null;
    }

    // Objektgr��e in Einheiten. Hier: Objekte.
    protected int getObjectSize(Value v) {
        return 1;
    }
}