package de.joglearth.caching;

public class TextureIdentifier
implements Cacheable {
	public int id;
	public int size;

	@Override
	public int getSize() {
		return size;
	}
	
}
