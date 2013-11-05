package de.JoglEarth.Model;

public interface CacheRequestListener<Key, Value> {
	void requestCompleted(Key k, Value v);
	
}
