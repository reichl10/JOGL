package de.joglearth.geometry;

import de.joglearth.caching.FileSystemCache;
import de.joglearth.caching.MemoryCache;
import de.joglearth.rendering.*;
import de.joglearth.source.*;
import de.joglearth.ui.*;

public class HeightMap {

	public HeightMap() {		
		FileSystemCache<Tile> fsCache = null;
		SRTMTileSource source = null;
		cache = new MemoryCache<Tile, byte[]>(new TileListener(), fsCache, source);
	}
	
	// Benachrichtigt den Renderer wenn eine neue Kachel ankommt.
	private class TileListener implements RequestListener<Tile, byte[]> {
		
		@Override
		public void requestCompleted(Tile k, byte[] v) {
			postUpdate();
		}
	}

	// Der (prim�re) Cache. K�mmert sich um den sekund�ren Plattencache und 
	// das Laden �ber HTTP (siehe Konstruktor von HeightMap)
	private MemoryCache<Tile, byte[]> cache;
	
	
	// Versucht die H�he an einem Punkt zu bestimmen, gibt 0 zur�ck, wenn die H�hendaten 
	// nicht im Cache sind.
	public float height(float longitude, float latitude) {
		/*l�ngen- breitengrad auf bogensekunden runden, tileCoordinate bestimmen
		 * kachel vom cache anfordern, h�henwert interpolieren(bestimmen).
		 */
		return 0;
	}
}