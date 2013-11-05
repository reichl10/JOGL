package de.JoglEarth.Model;

public class FileSystemCache<Key, Value> extends Cache<Key, Value> {
	
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
