package de.joglearth.model;

import javax.media.opengl.GL2;

import de.joglearth.model.*;
import de.joglearth.view.*;
import de.joglearth.controller.*;


public class TextureCache extends Cache<Tile, Integer, Integer> {

	private GL2 gl;
	
	public TextureCache(RequestListener<Tile, Integer> owner, GL2 gl) {
		super(owner);
		this.gl = gl;
	}

	@Override
	protected Integer addEntry(Integer v) {
		return null;
	}

	@Override
	protected void removeEntry(Integer r) {
	}

	@Override
	protected int getEntrySize(Integer r) {
		return 0;
	}

}
