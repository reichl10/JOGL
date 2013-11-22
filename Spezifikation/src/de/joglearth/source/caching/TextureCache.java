package de.joglearth.source.caching;

import javax.media.opengl.GL2;

import de.joglearth.geometry.Tile;
import de.joglearth.source.Source;
import de.joglearth.source.caching.MemoryCache;


public class TextureCache extends MemoryCache<Tile, Integer> {

	private GL2 gl;
	private Source<Tile, byte[]> imageSource;

	@Override
	public void dropObject(Tile k) {
		super.dropObject(k);
		// OpenGL-Textur freigeben
	}
}
