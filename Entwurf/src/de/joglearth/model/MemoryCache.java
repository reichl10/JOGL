package de.joglearth.model;

public class MemoryCache<Key, Value> extends Cache<Key, Value, Integer> {
	
	private Source<Key, Value> secondary, source;

	public MemoryCache(RequestListener<Key, Value> owner, 
			Source<Key, Value> secondary, 
			Source<Key, Value> source) {
		super(owner);
		this.secondary = secondary;
		this.source = source;
	}
	
	public void requestCompleted(Key k, Value v) {
		//put data into memory cache
		super.requestCompleted(k, v);
	}
	
	@Override
	public Value requestObject(Key k) {
		return null;
	}

	@Override
	protected Integer addEntry(Value v) {
		// TODO Automatisch erstellter Methoden-Stub
		return null;
	}	

	@Override
	protected void removeEntry(Integer r) {
		
	}

	@Override
	protected int getEntrySize(Integer r) {
		return 0;
	}

}
