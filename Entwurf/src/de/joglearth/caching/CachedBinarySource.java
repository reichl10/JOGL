package de.joglearth.caching;


public class CachedBinarySource<Key> extends CachedSource<Key, byte[]> {
	
	protected int getEntrySize(byte[] v) {
		return v.length;
	}
}
