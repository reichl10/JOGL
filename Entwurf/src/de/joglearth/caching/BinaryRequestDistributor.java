package de.joglearth.caching;


public class BinaryRequestDistributor<Key> extends RequestDistributor<Key, byte[]> {
	
	protected int getEntrySize(byte[] v) {
		return v.length;
	}
}
