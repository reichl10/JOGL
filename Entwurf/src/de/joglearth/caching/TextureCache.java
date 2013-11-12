package de.joglearth.caching;

import javax.media.opengl.GL2;

import de.joglearth.caching.MemoryCache;
import de.joglearth.geometry.Tile;
import de.joglearth.source.Source;


public class TextureCache extends MemoryCache<Tile, Integer> {

	private GL2 gl;
	private Source<Tile, byte[]> imageSource;

	@Override
	public void dropObject(Tile k) {
		super.dropObject(k);
		// OpenGL-Textur freigeben
	}
}
