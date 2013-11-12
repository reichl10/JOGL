package de.joglearth.geometry;

import de.joglearth.caching.FileSystemCache;
import de.joglearth.caching.MemoryCache;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SRTMTileSource;


public class HeightMap {

	// Der (prim�re) Cache. K�mmert sich um den sekund�ren Plattencache und 
	// das Laden �ber HTTP (siehe Konstruktor von HeightMap)
	private MemoryCache<Tile, byte[]> cache;

	public HeightMap() {		
		FileSystemCache<Tile> fsCache = null;
		SRTMTileSource source = null;
		cache = new MemoryCache<Tile, byte[]>();
	}
	
	private class TileListener implements SourceListener<Tile, byte[]> {
		
		@Override
		public void requestCompleted(Tile k, byte[] v) {
			
		}
	}
	
	
	// Versucht die H�he an einem Punkt zu bestimmen, gibt 0 zur�ck, wenn die H�hendaten 
	// nicht im Cache sind.
	public float height(float longitude, float latitude) {
		/*l�ngen- breitengrad auf bogensekunden runden, tileCoordinate bestimmen
		 * kachel vom cache anfordern, h�henwert interpolieren(bestimmen).
		 */
		return 0;
	}
}
