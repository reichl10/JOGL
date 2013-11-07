package de.joglearth.model;

import javax.media.opengl.GL2;

import de.joglearth.model.*;
import de.joglearth.view.*;
import de.joglearth.controller.*;


public class TextureCache extends Cache<Tile, Integer> {

	private GL2 gl;
	
	// Datenquelle, z.B. HTTPSource. 
	private Source<Tile, byte[]> imageSource;
	
	public TextureCache(RequestListener<Tile, Integer> owner, 
			Source<Tile, byte[]> imageSource, GL2 gl) {
		super(owner);
		this.gl = gl;
		this.imageSource = imageSource;
	}

	@Override
	protected void addEntry(Tile k, Integer v) {
	}

	@Override
	protected void removeEntry(Tile k) {
	}

	@Override
	protected int getEntrySize(Tile k) {
		return 0;
	}

}
