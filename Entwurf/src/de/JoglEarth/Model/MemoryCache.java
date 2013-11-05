package de.JoglEarth.Model;

public class MemoryCache<Key, Value> extends Cache<Key, Value> {

	private Cache<Key, Value> secondary;
	private Source<Key, Value> source;

	public MemoryCache(Cache<Key, Value> secondary, Source<Key, Value> source) {
		this.secondary = secondary;
		this.source = source;
	}
	
	public void requestCompleted(Key k, Value v) {
		
		//put data into memory cache
		super.requestCompleted(k, v);
	}
	
	@Override
	public Value requestObject(Key k, CacheRequestListener<Key, Value> l) {
		return null;
	}

	@Override
	public Value requestObject(Key k) {
		return null;
	}

	@Override
	public void putObject(Key k, Value v) {
		
	}

}
