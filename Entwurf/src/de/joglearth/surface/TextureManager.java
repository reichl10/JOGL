package de.joglearth.surface;

import de.joglearth.caching.CachedSource;
import de.joglearth.geometry.Tile;

public class TextureManager {

	private SurfaceNotifier notifier;
	private Integer placeholderTexture;
	private CachedSource<Tile, Integer> source;
	
	public Integer getTexture(Tile tile) {
		return placeholderTexture;
	}

	public void addSurfaceListener(SurfaceListener l) {
		notifier.addSurfaceListener(l);
	}
	
	public void removeSurfaceListener(SurfaceListener l) {
		notifier.removeSurfaceListener(l);
	}
	
	public int getPendingRequests() {
		return 0;
	}
}
