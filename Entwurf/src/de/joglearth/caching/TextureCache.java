package de.joglearth.caching;

import javax.media.opengl.GL2;

import de.joglearth.geometry.Tile;
import de.joglearth.rendering.*;
import de.joglearth.source.*;
import de.joglearth.ui.*;


public class TextureCache
extends Cache<Tile, TextureIdentifier> {

	private GL2 gl;
	
	// Datenquelle, z.B. HTTPSource. 
	private Source<Tile, CacheableBuffer> imageSource;
	
	public TextureCache(RequestListener<Tile, TextureIdentifier> owner, 
			Source<Tile, CacheableBuffer> imageSource, GL2 gl) {
		super(owner);
		this.gl = gl;
		this.imageSource = imageSource;
	}

	@Override
	protected void addEntry(Tile k, TextureIdentifier v) {
	}

	@Override
	protected void removeEntry(Tile k) {
	}

}
