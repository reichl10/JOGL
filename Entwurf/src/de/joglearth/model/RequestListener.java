package de.joglearth.model;

public interface RequestListener<Key, Value> {
	void requestCompleted(Key k, Value v);
	
}