package de.joglearth.caching;

public class CacheableBuffer implements Cacheable {

	public byte[] bytes;
	
	@Override
	public int getSize() {
		return bytes.length;
	}
}
