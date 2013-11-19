package de.joglearth.source;

/**
 * 
 *
 * @param <Key> identifier for the objects
 * @param <Value>
 */
public interface SourceListener<Key, Value> {
	void requestCompleted(Key k, Value v);
}
