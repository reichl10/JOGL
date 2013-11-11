package de.joglearth.source;

public interface RequestListener<Key, Value> {
	void requestCompleted(Key k, Value v);
	
}
