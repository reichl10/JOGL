package de.JoglEarth.Model;

public abstract class Source<Key, Value> {
	public abstract Value requestObject(Key k);
}
