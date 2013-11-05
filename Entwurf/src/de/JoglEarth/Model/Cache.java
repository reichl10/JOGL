package de.JoglEarth.Model;

public abstract class Cache<Key, Value> extends Source<Key, Value>
		implements CacheRequestListener<Key, Value> {

	private CacheRequestListener<Key, Value> owner;

	public void requestCompleted(Key k, Value v) {
		owner.requestCompleted(k, v);
	}
	
	public abstract void putObject(Key k, Value v);
	
	public abstract Value requestObject(Key k,
			CacheRequestListener<Key, Value> l);
	
}
