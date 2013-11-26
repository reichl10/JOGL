package de.joglearth.surface;

import javax.media.opengl.GL2;

import de.joglearth.geometry.Tile;
import de.joglearth.rendering.Tessellator;
import de.joglearth.settings.SettingsListener;
import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.caching.RequestDistributor;
import de.joglearth.source.opengl.TileMeshSource;
import de.joglearth.source.opengl.VertexBufferCache;

/**
 * Creates and cashes tile meshes as OpenGL vertex buffer objects.
 */
public class TileMeshManager implements Source<Tile, Integer> {

	private final int VERTEX_BUFFER_CACHE_SIZE = 50;

	private Tessellator tess;
	private int subdivisions;
	private RequestDistributor<Tile, Integer> dist;
	private VertexBufferCache cache;
	private TileMeshSource source;

	/**
	 * Creates a new {@link TileMeshManager} as it initializes the source, the cache, sets
	 * the {@link RequestDistributor} and adds a surface listener from {@link HeightMapManager}.
	 * @param gl The GL context
	 * @param t The {@link de.joglearth.rendering.Tessellator} that should be used
	 */
	public TileMeshManager(GL2 gl, Tessellator t) {
		source = new TileMeshSource(gl, t);
		cache = new VertexBufferCache(gl);
		dist = new RequestDistributor<Tile, Integer>();
		dist.setSource(source);
		dist.addCache(cache, VERTEX_BUFFER_CACHE_SIZE);

		HeightMapManager.getInstance().addSurfaceListener(
				new SurfaceListener() {
					@Override
					public void surfaceChanged(Tile tile) {
						dist.dropObject(tile);
						// notify listeners!
					}
				});
	}

	/**
	 * 
	 * @param t
	 */
	public void setTessellator(Tessellator t) {
		source.setTessellator(t);
		dist.dropAll();
	}

	/**
	 * 
	 * @param sub
	 */
	public void setTileSubdivisions(int sub) {
		source.setTileSubdivisions(sub);
		dist.dropAll();
	}

	/**
	 * 
	 * @param enable
	 */
	public void enableHeightMap(boolean enable) {
		source.enableHeightMap(enable);
		dist.dropAll();
	}

	@Override
	public SourceResponse<Integer> requestObject(Tile key,
			SourceListener<Tile, Integer> sender) {
		return null;
	}

	/**
	 * Adds a {@link SurfaceListener} that distributes a notification if the
	 * surface was changed.
	 * 
	 * @param l
	 *            The new listener
	 */
	public void addSurfaceListener(SurfaceListener l) {

	}

	/**
	 * Removes a specific {@link SurfaceListener}.
	 * 
	 * @param l
	 *            The listener that should be removed
	 */
	public void removeSurfaceListener(SurfaceListener l) {

	}

}
