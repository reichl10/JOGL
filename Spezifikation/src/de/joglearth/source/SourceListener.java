package de.joglearth.source;

public interface SourceListener<Key, Value> {
	void requestCompleted(Key k, Value v);
	
}
