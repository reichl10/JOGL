package de.JoglEarth.Controller;

import de.JoglEarth.Model.Cache;
import de.JoglEarth.Model.FileSystemCache;
import de.JoglEarth.Model.HTTPSource;
import de.JoglEarth.Model.MemoryCache;

public class HeightMap {
	
	private class TileCoordinates {
		public int longitude;
		public int latitude;
	}
	
	private MemoryCache<TileCoordinates, byte[]> cache;
	
	public HeightMap() {
		FileSystemCache<TileCoordinates, byte[]> fsCache = null;
		HTTPSource<TileCoordinates, byte[]> source = null;
		cache = new MemoryCache<TileCoordinates, byte[]>(fsCache, source);
	}
	
	public float height(float longitude, float latitude) {
		/*längen- breitengrad auf bogensekunden runden, tileCoordinate bestimmen
		 * kachel vom cache anfordern, höhenwert interpolieren(bestimmen).
		 */
		return 0;
	}
}