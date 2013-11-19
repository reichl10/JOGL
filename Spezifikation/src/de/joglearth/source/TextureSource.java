package de.joglearth.source;

import javax.media.opengl.GL2;

import de.joglearth.geometry.Tile;

public class TextureSource implements Source<Tile, Integer> {

	private GL2 gl;
	private Source<Tile, byte[]> imageSource;
	
	@Override
	public SourceResponse<Integer> requestObject(Tile key,
			SourceListener<Tile, Integer> sender) {
		// Textur in OpenGL laden
		return null;
	}

}