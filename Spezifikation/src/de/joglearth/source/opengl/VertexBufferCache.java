package de.joglearth.source.opengl;

import javax.media.opengl.GL2;

import de.joglearth.geometry.Tile;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.caching.Cache;

public class VertexBufferCache implements Cache<Tile, Integer> {

	private GL2 gl;
	
	public VertexBufferCache(GL2 gl) {
		this.gl = gl;
	}

	@Override
	public SourceResponse<Integer> requestObject(Tile key,
			SourceListener<Tile, Integer> sender) {
		// TODO Automatisch generierter Methodenstub
		return null;
	}

	@Override
	public void putObject(Tile k, Integer v) {
		// TODO Automatisch generierter Methodenstub
		
	}

	@Override
	public void dropObject(Tile k) {
		// TODO Automatisch generierter Methodenstub
		
	}

	@Override
	public Iterable<Tile> getExistingObjects() {
		return null;
	}

	@Override
	public void dropAll() {
		// TODO Automatisch generierter Methodenstub
		
	}

}
