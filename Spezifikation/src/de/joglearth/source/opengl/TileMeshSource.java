package de.joglearth.source.opengl;

import javax.media.opengl.GL2;

import de.joglearth.geometry.Tile;
import de.joglearth.rendering.Tessellator;
import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;

public class TileMeshSource implements Source<Tile, Integer> {

	private Tessellator tess;
	private GL2 gl;
	private int subdivisions;
	private boolean heightMap;
	
	public TileMeshSource(GL2 gl, Tessellator t) {
		this.gl = gl;
		tess = t;
	}
	
	public void setTessellator(Tessellator t) {
		tess = t;
	}
	
	public void setTileSubdivisions(int sub) {
		subdivisions = sub;
	}
	
	public void enableHeightMap(boolean enable) {
		heightMap = enable;
	}
	
	@Override
	public SourceResponse<Integer> requestObject(Tile key,
			SourceListener<Tile, Integer> sender) {
				return null;
		
	}

}
