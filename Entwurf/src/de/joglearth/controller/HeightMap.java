package de.joglearth.controller;

import de.joglearth.model.FileSystemCache;
import de.joglearth.model.HTTPSource;
import de.joglearth.model.MemoryCache;
import de.joglearth.model.RequestListener;
import de.joglearth.view.Renderer;


public class HeightMap {
	
	private static class TileCoordinates {
		public int longitude;
		public int latitude;
	}
	
	private class TileListener implements RequestListener<TileCoordinates, byte[]> {
		
		@Override
		public void requestCompleted(TileCoordinates k, byte[] v) {
			renderer.post();
		}
	}

	private Renderer renderer;
	
	private MemoryCache<TileCoordinates, byte[]> cache;
	
	public HeightMap(Renderer r) {
		FileSystemCache<TileCoordinates, byte[]> fsCache = null;
		HTTPSource<TileCoordinates, byte[]> source = null;
		cache = new MemoryCache<TileCoordinates, byte[]>(new TileListener(), fsCache, source);
		renderer = r;
	}
	
	public float height(float longitude, float latitude) {
		/*längen- breitengrad auf bogensekunden runden, tileCoordinate bestimmen
		 * kachel vom cache anfordern, h�henwert interpolieren(bestimmen).
		 */
		return 0;
	}
}